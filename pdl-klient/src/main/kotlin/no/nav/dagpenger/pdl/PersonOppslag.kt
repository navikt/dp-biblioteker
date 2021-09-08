package no.nav.dagpenger.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import kotlinx.coroutines.runBlocking
import no.nav.pdl.HentPersonNavnBolk
import no.nav.pdl.PersonBy
import no.nav.pdl.enums.ForelderBarnRelasjonRolle
import no.nav.pdl.hentpersonnavnbolk.HentPersonBolkResult
import no.nav.pdl.personby.Person
import java.net.URL

interface PersonOppslag {
    fun hentPerson(fnr: String): Person
    fun hentBarn(fnr: String): List<HentPersonBolkResult>
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
            client.execute(PersonBy(PersonBy.Variables(fnr)), requestBuilder).responseParser().hentPerson!!
        }

        override fun hentBarn(fnr: String) = runBlocking {
            hentPerson(fnr).forelderBarnRelasjon.filter { it.relatertPersonsRolle == ForelderBarnRelasjonRolle.BARN }
                .map { it.relatertPersonsIdent }
                .let {
                    client.execute(HentPersonNavnBolk(HentPersonNavnBolk.Variables(it)), requestBuilder)
                }.responseParser().hentPersonBolk
        }
    }
}
