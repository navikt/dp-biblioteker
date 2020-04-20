package no.nav.dagpenger.ktor.auth

import io.kotest.matchers.shouldBe
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Test

class ApiKeyAuthTest {

    @Test
    fun `Request with api key in header ok`() {
        withTestApplication({
            apiKeyHeader()
        }) {
            handleRequest(HttpMethod.Get, "/foo") {
                addHeader("X-API-KEY", "test")
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
            }
        }
    }

    @Test
    fun `Request with no api key in header is unauthorized`() {
        withTestApplication({
            apiKeyHeader()
        }) {
            handleRequest(HttpMethod.Get, "/foo") {
            }.apply {
                response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    @Test
    fun `Request with api key in query ok`() {
        withTestApplication({
            apiKeyQuery()
        }) {
            handleRequest(HttpMethod.Get, "/foo?apiKey=test") {
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
            }
        }
    }

    @Test
    fun `Request with no api key in query is unauthorized`() {
        withTestApplication({
            apiKeyQuery()
        }) {
            handleRequest(HttpMethod.Get, "/foo") {
            }.apply {
                response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }
}

fun Application.apiKeyHeader() {
    install(Authentication) {
        apiKeyAuth {
            apiKeyName = "X-API-KEY"
            validate { apikeyCredential: ApiKeyCredential ->
                when {
                    apikeyCredential.value == "test" -> ApiPrincipal(apikeyCredential)
                    else -> null
                }
            }
        }
    }

    routing {
        authenticate {
            route("/foo") {
                get {
                    call.respondText { "bar" }
                }
            }
        }
    }
}

fun Application.apiKeyQuery() {
    install(Authentication) {
        apiKeyAuth {
            apiKeyName = "apiKey"
            apiKeyLocation = ApiKeyLocation.QUERY
            validate { apikeyCredential: ApiKeyCredential ->
                when {
                    apikeyCredential.value == "test" -> ApiPrincipal(apikeyCredential)
                    else -> null
                }
            }
        }
    }

    routing {
        authenticate {
            route("/foo") {
                get {
                    call.respondText { "bar" }
                }
            }
        }
    }
}
