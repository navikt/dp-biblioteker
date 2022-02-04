package no.nav.dagpenger.pdl.adresse

import no.nav.dagpenger.pdl.TestPersonBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AdresseMetadataTest {
    private val today = LocalDate.now()
    private val yesterday = today.minusDays(1)
    private val tomorrow = today.plusDays(1)

    @Test
    fun `kan bygges fra bostedsadresse`() {
        AdresseMetadata.from(
            TestPersonBuilder.bostedsAdresse(
                master = "PDL",
                gyldigFom = yesterday,
                gyldigTom = tomorrow,
                matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
            )
        ).also {
            assertEquals(AdresseMetadata.AdresseType.BOSTEDSADRESSE, it.adresseType)
            assertEquals(AdresseMetadata.MasterType.PDL, it.master)
            assertEquals(yesterday..tomorrow, it.gyldighetsPeriode)
        }
    }

    @Test
    fun `kan bygges fra kontaktadresse`() {
        AdresseMetadata.from(
            TestPersonBuilder.kontaktAdresse(
                master = "PDL",
                gyldigFom = yesterday,
                gyldigTom = tomorrow,
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
                postadresseIFrittFormat = TestPersonBuilder.postadresseIFrittFormat(),
                postboksadresse = TestPersonBuilder.postboksadresse(),
            )
        ).also {
            assertEquals(AdresseMetadata.AdresseType.KONTAKTADRESSE, it.adresseType)
            assertEquals(AdresseMetadata.MasterType.PDL, it.master)
            assertEquals(yesterday..tomorrow, it.gyldighetsPeriode)
        }
    }

    @Test
    fun `kan bygges fra oppholdsadresse`() {
        AdresseMetadata.from(
            TestPersonBuilder.oppholdsAdresse(
                master = "PDL",
                gyldigFom = yesterday,
                gyldigTom = tomorrow,
                matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
            )
        ).also {
            assertEquals(AdresseMetadata.AdresseType.OPPHOLDSADRESSE, it.adresseType)
            assertEquals(AdresseMetadata.MasterType.PDL, it.master)
            assertEquals(yesterday..tomorrow, it.gyldighetsPeriode)
        }
    }

    @Test
    fun `Dersom gyldigFom og gyldigTom ikke settes brukes default verdier`() {
        AdresseMetadata.from(
            TestPersonBuilder.bostedsAdresse(
                master = "PDL",
                gyldigFom = null,
                gyldigTom = null,
                matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
            )
        ).also {
            assertEquals(LocalDate.MIN..LocalDate.MAX, it.gyldighetsPeriode)
        }
    }

    @Test
    fun `Seneste dato av gyldigFOM og angittFlyttedato brukes som registreringsDato`() {
        AdresseMetadata.from(
            TestPersonBuilder.bostedsAdresse(
                master = "PDL",
                gyldigFom = yesterday,
                gyldigTom = tomorrow,
                angittFlyttedato = today,
                matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
            )
        ).also {
            assertEquals(today..tomorrow, it.gyldighetsPeriode)
            assertEquals(today, it.registreringsDato)
        }
    }

    @Test
    fun `Gyldig kun dersom dagens dato er innenfor gyldighets perioden`() {
        AdresseMetadata.from(
            TestPersonBuilder.bostedsAdresse(
                master = "PDL",
                gyldigFom = yesterday,
                gyldigTom = tomorrow,
                matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
            )
        ).also {
            assertEquals(true, it.erGyldig)
        }

        AdresseMetadata.from(
            TestPersonBuilder.bostedsAdresse(
                master = "PDL",
                gyldigFom = yesterday.minusDays(1),
                gyldigTom = yesterday,
                matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
            )
        ).also {
            assertEquals(false, it.erGyldig)
        }

        AdresseMetadata.from(
            TestPersonBuilder.bostedsAdresse(
                master = "PDL",
                gyldigFom = tomorrow,
                matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
            )
        ).also {
            assertEquals(false, it.erGyldig)
        }

        AdresseMetadata.from(
            TestPersonBuilder.bostedsAdresse(
                master = "PDL",
                gyldigFom = today,
                gyldigTom = today,
                matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
            )
        ).also {
            assertEquals(true, it.erGyldig)
        }
    }

    @Test
    fun `Er norskbosteds adresse dersom adressen er en bosteds adresse og master er FREG`() {
        AdresseMetadata.from(
            TestPersonBuilder.bostedsAdresse(
                master = "FREG",
                matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse()
            )
        ).also {
            assertEquals(true, it.erNorskBostedsAdresse)
        }

        AdresseMetadata.from(
            TestPersonBuilder.kontaktAdresse(
                master = "FREG",
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
                postadresseIFrittFormat = TestPersonBuilder.postadresseIFrittFormat(),
                postboksadresse = TestPersonBuilder.postboksadresse()
            )
        ).also {
            assertEquals(false, it.erNorskBostedsAdresse)
        }

        AdresseMetadata.from(
            TestPersonBuilder.bostedsAdresse(
                master = "PDL",
                matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                utenlandskAdresse = TestPersonBuilder.utenlandskAdresse()
            )
        ).also {
            assertEquals(false, it.erNorskBostedsAdresse)
        }
    }
}
