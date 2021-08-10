package no.nav.dagpenger.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.pdl.BostedsAdresseMapper.bostedAdresseRegistrertDato
import no.nav.dagpenger.pdl.BostedsAdresseMapper.harGyldigNorskBostedAdresse
import no.nav.dagpenger.pdl.KontaktAdresseMapper.harGyldigKontaktAdresseFraFREG
import no.nav.dagpenger.pdl.KontaktAdresseMapper.harGyldigKontaktAdresseFraPDL
import no.nav.dagpenger.pdl.KontaktAdresseMapper.kontaktAdresseRegistrertDato
import no.nav.dagpenger.pdl.OppholdsAdresseMapper.harGyldigOppholdsAdresseFraFREG
import no.nav.dagpenger.pdl.OppholdsAdresseMapper.harGyldigOppholdsAdresseFraPDL
import no.nav.dagpenger.pdl.OppholdsAdresseMapper.oppHoldsAdresseRegistrertDato
import no.nav.pdl.HentPerson
import no.nav.pdl.hentperson.Person
import java.net.URL

interface PersonOppslag {
    fun hentPerson(fnr: String): Person
    fun hentPostAdresse(fnr: String): PostAddresse
}

interface AdresseMapper {
    fun map(person: Person): PostAddresse
}

object Hubba1 : AdresseMapper {
    override fun map(person: Person): PostAddresse {
        TODO("Not yet implemented")
    }
}

enum class AddresseStrategy(private val mapper: AdresseMapper) {
    @Suppress("unused")
    BOSTEDADRESSSE_NYERE_ENN_ANDRE_ADDRESSER(Hubba1) {
        override fun valid(person: Person) = person.harGyldigNorskBostedAdresse && person.bostedAdresseRegistrertDato > maxOf(person.kontaktAdresseRegistrertDato, person.oppHoldsAdresseRegistrertDato)
    },

    @Suppress("unused")
    KONTAKTADRSSE_MED_MASTER_PDL(Hubba1) {
        override fun valid(person: Person): Boolean = person.harGyldigKontaktAdresseFraPDL
    },

    @Suppress("unused")
    KONTAKTADRSSE_MED_MASTER_FREG(Hubba1) {
        override fun valid(person: Person): Boolean = person.harGyldigKontaktAdresseFraFREG
    },

    @Suppress("unused")
    OPPHOLDSADRESSE_MED_MASTER_PDL(Hubba1) {
        override fun valid(person: Person): Boolean = person.harGyldigOppholdsAdresseFraPDL
    },

    @Suppress("unused")
    OPPHOLDSADRESSE_MED_MASTER_FREG(Hubba1) {
        override fun valid(person: Person): Boolean = person.harGyldigOppholdsAdresseFraFREG
    },

    @Suppress("unused")
    BOSTED_ADRESSE(Hubba1) {
        override fun valid(person: Person): Boolean {
            TODO("Not yet implemented")
        }
    };

    protected abstract fun valid(person: Person): Boolean
    fun addresse(person: Person): PostAddresse {
        return mapper.map(person)
    }

    object Decider {
        fun decide(person: Person): AddresseStrategy {
            return values().firstOrNull { it.valid(person) } ?: throw NoSuchElementException()
        }
    }
}

data class PostAddresse(
    val navn: String,
    val hubba: String,
    val sted: String?,
    val vei: String?
)

@JvmOverloads
fun createPersonOppslag(
    url: String,
    requestBuilder: HttpRequestBuilder.() -> Unit,
    httpClient: HttpClient = HttpClient(engineFactory = CIO)
): PersonOppslag {
    return object : PersonOppslag {
        private val client = GraphQLKtorClient(
            url = URL(url),
            httpClient = httpClient
        )

        override fun hentPerson(fnr: String): Person = runBlocking {
            client.execute(HentPerson(HentPerson.Variables(fnr)), requestBuilder).responseParser().hentPerson!!
        }

        override fun hentPostAdresse(fnr: String): PostAddresse {
            val person = hentPerson(fnr)
            return AddresseStrategy.Decider.decide(person).addresse(person)
        }
    }
}
