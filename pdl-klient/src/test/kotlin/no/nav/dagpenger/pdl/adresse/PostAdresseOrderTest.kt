package no.nav.dagpenger.pdl.adresse

import no.nav.dagpenger.pdl.adresse.AdresseMetadata.AdresseType
import no.nav.dagpenger.pdl.adresse.AdresseMetadata.MasterType
import no.nav.dagpenger.pdl.adresse.AdresseMetadata.MasterType.FREG
import no.nav.dagpenger.pdl.adresse.AdresseMetadata.MasterType.PDL
import no.nav.dagpenger.pdl.adresse.PostAdresseOrder.postAdresser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.random.Random

class PostAdresseOrderTest {
    private val today: LocalDate = LocalDate.now()
    private val yesterday: LocalDate = today.minusDays(1)
    private val beforeYesterday: LocalDate = today.minusDays(2)

    @Test
    fun `Sorter etter adresseType, master og registreringsdato deretter nyeste norsk bostedsadresse`() {
        val expected = listOf(
            pdlAdresse(adresseType = AdresseType.KONTAKTADRESSE, master = PDL, gyldigFom = yesterday),
            pdlAdresse(adresseType = AdresseType.KONTAKTADRESSE, master = PDL, gyldigFom = beforeYesterday),
            pdlAdresse(adresseType = AdresseType.KONTAKTADRESSE, master = FREG, gyldigFom = yesterday),
            pdlAdresse(adresseType = AdresseType.KONTAKTADRESSE, master = FREG, gyldigFom = beforeYesterday),
            pdlAdresse(adresseType = AdresseType.OPPHOLDSADRESSE, master = PDL, gyldigFom = yesterday),
            pdlAdresse(adresseType = AdresseType.OPPHOLDSADRESSE, master = PDL, gyldigFom = beforeYesterday),
            pdlAdresse(adresseType = AdresseType.OPPHOLDSADRESSE, master = FREG, gyldigFom = yesterday),
            pdlAdresse(adresseType = AdresseType.OPPHOLDSADRESSE, master = FREG, gyldigFom = beforeYesterday),
            pdlAdresse(adresseType = AdresseType.BOSTEDSADRESSE, master = PDL, gyldigFom = yesterday),
            pdlAdresse(adresseType = AdresseType.BOSTEDSADRESSE, master = PDL, gyldigFom = beforeYesterday),
            pdlAdresse(adresseType = AdresseType.BOSTEDSADRESSE, master = FREG, gyldigFom = today),
            pdlAdresse(adresseType = AdresseType.BOSTEDSADRESSE, master = FREG, gyldigFom = yesterday),
            pdlAdresse(adresseType = AdresseType.BOSTEDSADRESSE, master = FREG, gyldigFom = beforeYesterday),
        )

        (1..10).forEach {
            val sorted: List<PDLAdresse> = expected.shuffled(Random(it)).postAdresser()
            assertMetadataEquals(expected, sorted)
        }
    }

    private fun pdlAdresse(
        adresseType: AdresseType,
        type: String? = null,
        gyldigFom: LocalDate? = null,
        gyldigTom: LocalDate? = null,
        angittFlyttedato: LocalDate? = null,
        master: MasterType = PDL
    ): PDLAdresse {
        return PDLAdresse.PostboksAdresse(
            AdresseMetadata(
                adresseType,
                type,
                gyldigFom,
                gyldigTom,
                angittFlyttedato,
                master
            ),
            null,
            null,
            null
        )
    }

    private fun assertMetadataEquals(
        expected: List<PDLAdresse>,
        actual: List<PDLAdresse>
    ) {
        assertEquals(expected.size, actual.size, "Diff i liste størrelse")
        expected.forEachIndexed { i, e ->
            assertMetadataEquals(e.adresseMetadata, actual[i].adresseMetadata)
        }
    }

    private fun assertMetadataEquals(
        expected: AdresseMetadata,
        actual: AdresseMetadata
    ) {
        assertEquals(expected.adresseType, actual.adresseType, "Diff i addresseType")
        assertEquals(expected.master, actual.master, "Diff i master")
        assertEquals(expected.gyldighetsPeriode, actual.gyldighetsPeriode, "Diff i gyldighets periode")
    }
}
