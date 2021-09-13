package no.nav.dagpenger.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import kotlinx.coroutines.runBlocking
import no.nav.pdl.PersonBy
import no.nav.pdl.PersonerBy
import no.nav.pdl.personby.Person
import no.nav.pdl.personerby.HentPersonBolkResult
import java.net.URL

interface PersonOppslag {
    fun hentPerson(fnr: String): Person
    fun hentPersoner(fnrs: Set<String>): List<HentPersonBolkResult>
}

@JvmOverloads
fun createPersonOppslag(
    url: String,
    requestBuilder: HttpRequestBuilder.() -> Unit,
    httpClient: HttpClient = defaultHttpClient
): PersonOppslag {
    return object : PersonOppslag {
        private val client = GraphQLKtorClient(
            url = URL(url),
            httpClient = httpClient
        )

        override fun hentPerson(fnr: String): Person = runBlocking {
            hentPersonSuspendable(fnr)
        }

        private suspend fun hentPersonSuspendable(fnr: String): Person {
            return client.execute(PersonBy(PersonBy.Variables(fnr)), requestBuilder).responseParser().hentPerson!!
        }

        private suspend fun hentPersonerSuspendable(fnrs: List<String>): List<HentPersonBolkResult> {
            return client.execute(PersonerBy(PersonerBy.Variables(fnrs)), requestBuilder).responseParser().hentPersonBolk
        }

        override fun hentPersoner(fnrs: Set<String>): List<HentPersonBolkResult> = runBlocking {
            hentPersonerSuspendable(fnrs.toList())
        }
    }
}
