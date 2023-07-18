package no.nav.dagpenger.pdl

import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.pdl.adapter.KtorHttpClientAdapter
import no.nav.dagpenger.pdl.adapter.proxyAwareHttpClient
import no.nav.dagpenger.pdl.dto.ForelderBarnRelasjonRolle
import no.nav.dagpenger.pdl.queries.hentPerson
import no.nav.dagpenger.pdl.queries.hentPersonBolk

interface PersonOppslag {
    suspend fun hentPerson(fnr: String, headersMap: Map<String, String> = emptyMap()): PDLPerson
    fun hentPersonBlocking(fnr: String, headersMap: Map<String, String>): PDLPerson
}

interface PersonOppslagBolk {
    suspend fun hentPersoner(fnrs: List<String>, headersMap: Map<String, String> = emptyMap()): List<PDLPerson>
    suspend fun hentBarn(fnr: String, headersMap: Map<String, String> = emptyMap()): List<PDLPerson>
    fun hentPersonerBlocking(fnrs: List<String>, headersMap: Map<String, String>): List<PDLPerson>
    fun hentBarnBlocking(fnr: String, headersMap: Map<String, String>): List<PDLPerson>
}

@JvmOverloads
fun createPersonOppslagBolk(
    url: String,
    httpClient: HttpClient = proxyAwareHttpClient(),
): PersonOppslagBolk {
    return object : PersonOppslagBolk {
        override suspend fun hentPersoner(fnrs: List<String>, headersMap: Map<String, String>): List<PDLPerson> {
            val pdlContext = pdlContextOf(KtorHttpClientAdapter(url, headersMap, httpClient))
            return pdlContext.query {
                hentPersonBolk(fnrs)
            }
                .hentPersonBolk
                .mapNotNull { it.person }
                .map(::PDLPerson)
        }

        override suspend fun hentBarn(fnr: String, headersMap: Map<String, String>): List<PDLPerson> {
            val pdlContext = pdlContextOf(KtorHttpClientAdapter(url, headersMap, httpClient))
            val barn = pdlContext.query {
                hentPersonBolk(listOf(fnr))
            }
                .hentPersonBolk
                .mapNotNull { it.person }
                .single()
                .forelderBarnRelasjon
                .filter {
                    it.relatertPersonsRolle == ForelderBarnRelasjonRolle.BARN
                }.mapNotNull { it.relatertPersonsIdent }

            return if (barn.isEmpty()) {
                emptyList()
            } else
                pdlContext.query {
                    hentPersonBolk(barn)
                }
                    .hentPersonBolk
                    .mapNotNull {
                        it.person
                    }
                    .filter {
                        it.doedsfall.isEmpty()
                    }
                    .map(::PDLPerson)
        }

        override fun hentPersonerBlocking(fnrs: List<String>, headersMap: Map<String, String>): List<PDLPerson> =
            runBlocking { hentPersoner(fnrs, headersMap) }

        override fun hentBarnBlocking(fnr: String, headersMap: Map<String, String>): List<PDLPerson> {
            return runBlocking { hentBarn(fnr, headersMap) }
        }
    }
}

@JvmOverloads
fun createPersonOppslag(
    url: String,
    httpClient: HttpClient = proxyAwareHttpClient(),
): PersonOppslag {
    return object : PersonOppslag {
        override suspend fun hentPerson(fnr: String, headersMap: Map<String, String>): PDLPerson {
            val pdlContext = pdlContextOf(KtorHttpClientAdapter(url, headersMap, httpClient))

            return pdlContext.query { hentPerson(fnr) }
                .hentPerson
                ?.let(::PDLPerson)
                ?: throw PDLPerson.PDLException("Ukjent feil")
        }

        override fun hentPersonBlocking(fnr: String, headersMap: Map<String, String>): PDLPerson {
            return runBlocking { hentPerson(fnr, headersMap) }
        }
    }
}
