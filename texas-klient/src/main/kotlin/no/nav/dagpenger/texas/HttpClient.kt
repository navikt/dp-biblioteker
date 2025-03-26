package no.nav.dagpenger.texas

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson

fun defaultHttpClient(
    httpClientEngine: HttpClientEngine = CIO.create {},
    configure: List<HttpClientConfig<*>.() -> Unit> = listOf(defaultPlugins()),
) = HttpClient(httpClientEngine) {
    expectSuccess = true

    install(ContentNegotiation) {
        jackson {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            registerModules(
                SimpleModule().also {
                    it.addDeserializer(IntrospectResponse::class.java, IntrospectResponseDeserializer)
                },
            )
        }
    }

    HttpResponseValidator {
        handleResponseException { cause, _ ->
            when (cause) {
                is ClientRequestException -> {
                    when (val statusCode = cause.response.status) {
                        HttpStatusCode.BadRequest -> {
                            val errorResponse = cause.response.body<ErrorResponse>()
                            throw BadRequestException(statusCode, errorResponse)
                        }
                    }
                }

                is ServerResponseException -> {
                    when (val statusCode = cause.response.status) {
                        HttpStatusCode.InternalServerError -> {
                            val errorResponse = cause.response.body<ErrorResponse>()
                            throw ServerError(statusCode, errorResponse)
                        }
                    }
                }
            }
        }
    }
    configure.forEach { it() }
}

fun defaultPlugins(): HttpClientConfig<*>.() -> Unit =
    {
        install(HttpRequestRetry) {
            retryOnException(maxRetries = 3, retryOnTimeout = true)
            exponentialDelay()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 5000
        }
    }
