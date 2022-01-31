package no.nav.dagpenger.pdl.adresse

import no.nav.dagpenger.pdl.PDLPerson
import no.nav.dagpenger.pdl.TestPersonBuilder
import no.nav.dagpenger.pdl.adresse.AdresseMetadata.AdresseType.BOSTEDSADRESSE
import no.nav.dagpenger.pdl.adresse.AdresseMetadata.AdresseType.KONTAKTADRESSE
import no.nav.dagpenger.pdl.adresse.AdresseMetadata.AdresseType.OPPHOLDSADRESSE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class AdresseVisitorTest {

    @Test
    fun `Tom adresse hvis personen har fortrolig adresse`() {
        listOf(
            PDLPerson.AdressebeskyttelseGradering.FORTROLIG,
            PDLPerson.AdressebeskyttelseGradering.STRENGT_FORTROLIG,
            PDLPerson.AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND,
        ).forEach { gradering ->
            AdresseVisitor(
                TestPersonBuilder(
                    adressebeskyttelseGradering = gradering.name,
                    bostedsAdresser = listOf(
                        TestPersonBuilder.bostedsAdresse(
                            vegadresse = TestPersonBuilder.vegadresse(
                                postnummer = "2013"
                            )
                        )
                    )

                ).pdlPerson
            ).also {
                assertEquals(emptyList<PDLAdresse>(), it.adresser, "$gradering")
            }
        }
    }

    @Test
    fun `tom adresse dersom ingen adresse er gyldige`() {
        AdresseVisitor(
            TestPersonBuilder(
                bostedsAdresser = listOf(
                    TestPersonBuilder.bostedsAdresse(
                        gyldigTom = LocalDate.now().minusDays(1),
                        vegadresse = TestPersonBuilder.vegadresse(
                            postnummer = "2013"
                        )
                    )
                ),
                kontaktAdresser = listOf(
                    TestPersonBuilder.kontaktAdresse(
                        gyldigTom = LocalDate.now().minusDays(1),
                        utenlandskAdresseIFrittFormat = TestPersonBuilder.utenlandskAdresseIFrittFormat()
                    )
                ),
                oppholdAdresser = listOf(
                    TestPersonBuilder.oppholdsAdresse(
                        gyldigTom = LocalDate.now().minusDays(1),
                        vegadresse = TestPersonBuilder.vegadresse(
                            postnummer = "2013"
                        )
                    )
                )
            ).pdlPerson
        ).let {
            assertEquals(emptyList<PDLAdresse>(), it.adresser)
        }
    }

    @Test
    fun `Henter ut alle typer av adresser`() {
        val tomorrow = LocalDate.now().plusDays(1)
        AdresseVisitor(
            TestPersonBuilder(
                bostedsAdresser = listOf(
                    TestPersonBuilder.bostedsAdresse(
                        gyldigTom = tomorrow,
                        vegadresse = TestPersonBuilder.vegadresse(),
                        matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                        utenlandskAdresse = TestPersonBuilder.utenlandskAdresse()
                    )
                ),
                kontaktAdresser = listOf(
                    TestPersonBuilder.kontaktAdresse(
                        gyldigTom = tomorrow,
                        utenlandskAdresseIFrittFormat = TestPersonBuilder.utenlandskAdresseIFrittFormat(),
                        utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
                        postadresseIFrittFormat = TestPersonBuilder.postadresseIFrittFormat(),
                        postboksadresse = TestPersonBuilder.postboksadresse()
                    )
                ),
                oppholdAdresser = listOf(
                    TestPersonBuilder.oppholdsAdresse(
                        gyldigTom = tomorrow,
                        vegadresse = TestPersonBuilder.vegadresse(),
                        matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                        utenlandskAdresse = TestPersonBuilder.utenlandskAdresse()
                    )
                )
            ).pdlPerson
        ).let { visitor ->
            assertEquals(10, visitor.adresser.size)
            assertEquals(3, visitor.adresser.filter { it.adresseMetadata.adresseType == BOSTEDSADRESSE }.size)
            assertEquals(4, visitor.adresser.filter { it.adresseMetadata.adresseType == KONTAKTADRESSE }.size)
            assertEquals(3, visitor.adresser.filter { it.adresseMetadata.adresseType == OPPHOLDSADRESSE }.size)
            assertEquals(2, visitor.adresser.filterIsInstance<PDLAdresse.VegAdresse>().size)
            assertEquals(2, visitor.adresser.filterIsInstance<PDLAdresse.MatrikkelAdresse>().size)
            assertEquals(3, visitor.adresser.filterIsInstance<PDLAdresse.UtenlandskAdresse>().size)
            assertEquals(1, visitor.adresser.filterIsInstance<PDLAdresse.UtenlandsAdresseIFrittFormat>().size)
            assertEquals(1, visitor.adresser.filterIsInstance<PDLAdresse.PostAdresseIFrittFormat>().size)
            assertEquals(1, visitor.adresser.filterIsInstance<PDLAdresse.PostboksAdresse>().size)
        }
    }
}
