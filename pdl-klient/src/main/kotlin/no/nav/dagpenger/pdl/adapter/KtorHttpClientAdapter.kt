package no.nav.dagpenger.pdl.adapter

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import no.nav.dagpenger.pdl.PDLPerson
import no.nav.dagpenger.pdl.PdlAdapter
import no.nav.dagpenger.pdl.dto.QueryDto
import no.nav.dagpenger.pdl.dto.graphql.PdlQueryResult
import no.nav.dagpenger.pdl.dto.graphql.PdlRequest

class KtorHttpClientAdapter(
    private val urlString: String,
    private val headersMap: Map<String, String>,
    private val httpClient: HttpClient = proxyAwareHttpClient()
) : PdlAdapter {
    override suspend fun executeQuery(query: String, variables: Map<String, Any?>): QueryDto {
        return httpClient.post {
            setBody(PdlRequest(query, variables))
            contentType(ContentType.Application.Json)
            url(urlString)
            for (element in headersMap) {
                this.headers[element.key] = element.value
            }
        }.let { responseParser(it.body()) }
    }

    private fun responseParser(result: PdlQueryResult): QueryDto {
        if (!result.errors.isNullOrEmpty()) {
            result.errors!!.asSequence().map { it.message }.joinToString(";").let {
                throw PDLPerson.PDLException(it)
            }
        }
        return when (result.data) {
            null -> throw PDLPerson.PDLException("No data")
            else -> result.data!!
        }
    }
}

fun proxyAwareHttpClient(engine: HttpClientEngine = CIO.create()) = HttpClient(engine) {
    install(ContentNegotiation) {
        jackson {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            registerModules(JavaTimeModule())
        }
    }
    defaultRequest {
        header("TEMA", "DAG")
    }

    engine {
        System.getenv("HTTP_PROXY")?.let {
            this.proxy = ProxyBuilder.http(Url(it))
        }
    }
}
