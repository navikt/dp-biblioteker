package no.nav.dagpenger.oauth2

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.http
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson

@JvmOverloads
fun defaultHttpClient(httpClientEngine: HttpClientEngine = CIO.create()) =
    HttpClient(httpClientEngine) {
        expectSuccess = true
        install(ContentNegotiation) {
            jackson {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }
        }
        engine {
            System.getenv("HTTP_PROXY")?.let {
                this.proxy = ProxyBuilder.http(it)
            }
        }
    }
