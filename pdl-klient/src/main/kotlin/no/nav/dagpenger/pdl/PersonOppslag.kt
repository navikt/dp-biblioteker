package no.nav.dagpenger.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.runBlocking
import no.nav.pdl.PersonBy
import no.nav.pdl.personby.Person
import java.net.URL

interface PersonOppslag {
    fun hentPerson(fnr: String): Person
    fun hentPersoner(fnrs: Set<String>): Set<Person>
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

        override fun hentPersoner(fnrs: Set<String>): Set<Person> = runBlocking {
            fnrs.asFlow().map { hentPersonSuspendable(it) }.toSet()
        }
    }
}
