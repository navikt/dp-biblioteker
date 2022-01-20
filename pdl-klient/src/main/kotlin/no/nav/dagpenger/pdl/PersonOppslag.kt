package no.nav.dagpenger.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import kotlinx.coroutines.runBlocking
import no.nav.pdl.PersonBy
import no.nav.pdl.enums.ForelderBarnRelasjonRolle
import no.nav.pdl.personby.Person
import java.net.URL

interface PersonOppslag {
    fun hentPerson(fnr: String): Person
    fun hentBarn(fnr: String): List<Person>
}

interface PersonOppslagSuspendable {
    suspend fun hentPerson(fnr: String): Person
    suspend fun hentBarn(fnr: String): List<Person>
}

fun createPersonOppslagSuspendable(
    url: String,
    requestBuilder: HttpRequestBuilder.() -> Unit,
    httpClient: HttpClient = defaultHttpClient
): PersonOppslagSuspendable {
    return object : PersonOppslagSuspendable {
        private val client = GraphQLKtorClient(
            url = URL(url),
            httpClient = httpClient
        )

        override suspend fun hentPerson(fnr: String): Person {
            return this.hentPersoner(listOf(fnr)).single()
        }

        private suspend fun hentPersoner(fnrs: List<String>): List<Person> {
            return client.execute(PersonBy(PersonBy.Variables(fnrs)), requestBuilder)
                .responseParser().hentPersonBolk
                .mapNotNull { it.person }
        }

        override suspend fun hentBarn(fnr: String): List<Person> {
            return hentPerson(fnr)
                .forelderBarnRelasjon
                .filter { it.relatertPersonsRolle == ForelderBarnRelasjonRolle.BARN }
                .map { it.relatertPersonsIdent }
                .takeIf { it.isNotEmpty() }
                ?.let { hentPersoner(it) }
                ?.filter { it.doedsfall.isEmpty() } ?: emptyList()
        }
    }
}

@JvmOverloads
fun createPersonOppslag(
    url: String,
    requestBuilder: HttpRequestBuilder.() -> Unit,
    httpClient: HttpClient = defaultHttpClient
): PersonOppslag {
    return object : PersonOppslag {
        private val client = createPersonOppslagSuspendable(url, requestBuilder, httpClient)

        override fun hentPerson(fnr: String): Person = runBlocking { client.hentPerson(fnr) }

        override fun hentBarn(fnr: String): List<Person> = runBlocking {
            client.hentBarn(fnr)
        }
    }
}
