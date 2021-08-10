package no.nav.dagpenger.pdl

import no.nav.dagpenger.pdl.BostedsAdresseMapper.harGyldigNorskBostedAdresse
import no.nav.pdl.hentperson.Bostedsadresse
import no.nav.pdl.hentperson.Person
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AdresseHelperTest {

    @Test
    fun `BostedAdresse mappings`() {
        val tomBostedsadresse = Bostedsadresse(
            angittFlyttedato = null,
            coAdressenavn = null,
            folkeregistermetadata = null,
            gyldigFraOgMed = null,
            gyldigTilOgMed = null,
            matrikkeladresse = null,
            metadata = no.nav.pdl.hentperson.Metadata(
                endringer = listOf(),
                master = "PDL",
                opplysningsId = null,
                historisk = false
            ),
            ukjentBosted = null,
            utenlandskAdresse = null,
            vegadresse = null
        )

        val person = Person(
            folkeregisterpersonstatus = emptyList(),
            navn = emptyList(),
            adressebeskyttelse = emptyList(),
            kontaktadresse = emptyList(),
            oppholdsadresse = emptyList(),
            bostedsadresse = listOf(
                tomBostedsadresse.copy()
            )
        )
        assertEquals(false, person.harGyldigNorskBostedAdresse)
    }
}
