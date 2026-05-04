package no.nav.dagpenger.oauth2

import com.fasterxml.jackson.annotation.JsonInclude
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.http
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson3.jackson

@JvmOverloads
fun defaultHttpClient(
    httpClientEngine: HttpClientEngine =
        CIO.create {
            System.getenv("HTTP_PROXY")?.let {
                this.proxy = ProxyBuilder.http(it)
            }
        },
) = HttpClient(httpClientEngine) {
    expectSuccess = true
    install(ContentNegotiation) {
        jackson {
            changeDefaultPropertyInclusion {
                JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.USE_DEFAULTS)
            }
        }
    }
}
