package no.nav.dagpenger.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import kotlinx.coroutines.runBlocking
import no.nav.pdl.PersonBy
import no.nav.pdl.enums.ForelderBarnRelasjonRolle
import no.nav.pdl.personby.Person
import java.net.URL

interface PersonoppslagBlocking {
    fun hentPerson(fnr: String): PDLPerson
    fun hentBarn(fnr: String): List<PDLPerson>
}

interface PersonOppslag {
    suspend fun hentPerson(fnr: String): PDLPerson
    suspend fun hentBarn(fnr: String): List<PDLPerson>
}

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

        override suspend fun hentPerson(fnr: String): PDLPerson {
            return PDLPerson(person(fnr))
        }

        override suspend fun hentBarn(fnr: String): List<PDLPerson> {
            return person(fnr)
                .forelderBarnRelasjon
                .filter { it.relatertPersonsRolle == ForelderBarnRelasjonRolle.BARN }
                .map { it.relatertPersonsIdent }
                .takeIf { it.isNotEmpty() }
                ?.let { hentPersoner(it) }
                ?.filter { it.doedsfall.isEmpty() }
                ?.map(::PDLPerson)
                ?: emptyList()
        }

        private suspend fun person(fnr: String): Person {
            return this.hentPersoner(listOf(fnr)).single()
        }

        private suspend fun hentPersoner(fnrs: List<String>): List<Person> {
            return client.execute(PersonBy(PersonBy.Variables(fnrs)), requestBuilder)
                .responseParser().hentPersonBolk
                .mapNotNull { it.person }
        }
    }
}

@JvmOverloads
fun createPersonOppslagBlocking(
    url: String,
    requestBuilder: HttpRequestBuilder.() -> Unit,
    httpClient: HttpClient = defaultHttpClient
): PersonoppslagBlocking {
    return object : PersonoppslagBlocking {
        private val client = createPersonOppslag(url, requestBuilder, httpClient)

        override fun hentPerson(fnr: String): PDLPerson = runBlocking { client.hentPerson(fnr) }

        override fun hentBarn(fnr: String): List<PDLPerson> = runBlocking {
            client.hentBarn(fnr)
        }
    }
}
