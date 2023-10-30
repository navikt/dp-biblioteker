package no.nav.dagpenger.pdl

import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import no.nav.dagpenger.pdl.adresse.AdresseMetadata
import no.nav.dagpenger.pdl.adresse.PDLAdresse
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class PDLPersonTest {
    private val today = LocalDate.now()
    private val yesterday = today.minusDays(1)

    @Test
    fun `aksepterer personalia visitor`() {
        val visitor = mockk<PersonaliaVisitor>(relaxed = true)

        val testPersonBuilder = TestPersonBuilder()

        testPersonBuilder.testPerson.acceptPersonaliaVisitor(visitor)

        verify {
            visitor.visit(
                fodselnummer = testPersonBuilder.fnr,
                fodselsdato = testPersonBuilder.fodselsdato,
                alder = ChronoUnit.YEARS.between(testPersonBuilder.fodselsdato, LocalDate.now()),
                adressebeskyttelseGradering = PDLPerson.AdressebeskyttelseGradering.UGRADERT,
                fornavn = testPersonBuilder.fornavn,
                mellomNavn = testPersonBuilder.mellomnavn,
                etternavn = testPersonBuilder.etternavn,
                statsborgerskap = testPersonBuilder.statsborgerskap.first(),
                kjonn = PDLPerson.Kjonn.KVINNE,
            )
        }
    }

    @Test
    fun `akspeterer oppholdsadresse visitor`() {
        val visitor = mockk<OppholdsAdresseVisitor>(relaxed = true)
        val metadata =
            AdresseMetadata(
                adresseType = AdresseMetadata.AdresseType.OPPHOLDSADRESSE,
                gyldigFom = yesterday,
                gyldigTom = today,
                angittFlytteDato = null,
                master = AdresseMetadata.MasterType.PDL,
                type = null,
                coAdresseNavn = null,
            )

        val pdlPerson =
            TestPersonBuilder(
                oppholdAdresser =
                    listOf(
                        TestPersonBuilder.oppholdsAdresse(
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                        ),
                        TestPersonBuilder.oppholdsAdresse(
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            vegadresse = TestPersonBuilder.vegadresse(),
                        ),
                        TestPersonBuilder.oppholdsAdresse(
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
                        ),
                    ),
            ).testPerson

        pdlPerson.acceptOppholdsAdressseVisitor(visitor)
        verify {
            martikkelAdresse(metadata, visitor)
            vegadresse(metadata, visitor)
            utenlandskAdresse(metadata, visitor)
        }
        confirmVerified(visitor)
    }

    @Test
    fun `aksepterer kontaktadresse visitor`() {
        val visitor = mockk<KontaktAdresseVisitor>(relaxed = true)
        val metadata =
            AdresseMetadata(
                adresseType = AdresseMetadata.AdresseType.KONTAKTADRESSE,
                gyldigFom = yesterday,
                gyldigTom = today,
                angittFlytteDato = null,
                master = AdresseMetadata.MasterType.PDL,
                type = "Innland",
                coAdresseNavn = null,
            )

        val pdlPerson =
            TestPersonBuilder(
                kontaktAdresser =
                    listOf(
                        TestPersonBuilder.kontaktAdresse(
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            utenlandskAdresseIFrittFormat = TestPersonBuilder.utenlandskAdresseIFrittFormat(),
                        ),
                        TestPersonBuilder.kontaktAdresse(
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
                        ),
                        TestPersonBuilder.kontaktAdresse(
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            vegadresse = TestPersonBuilder.vegadresse(),
                        ),
                        TestPersonBuilder.kontaktAdresse(
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            postadresseIFrittFormat = TestPersonBuilder.postadresseIFrittFormat(),
                        ),
                        TestPersonBuilder.kontaktAdresse(
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            postboksadresse = TestPersonBuilder.postboksadresse(),
                        ),
                    ),
            ).testPerson

        pdlPerson.acceptKontaktAdresseVisitor(visitor)
        verify {
            val utenlandsAdresseIFrittFormat = TestPersonBuilder.utenlandskAdresseIFrittFormat()
            visitor.visitUtenlandskAdresseIFrittFormat(
                adresse =
                    PDLAdresse.UtenlandsAdresseIFrittFormat(
                        adresseMetadata = metadata,
                        adresseLinje1 = utenlandsAdresseIFrittFormat.adresselinje1,
                        adresseLinje2 = utenlandsAdresseIFrittFormat.adresselinje2,
                        adresseLinje3 = utenlandsAdresseIFrittFormat.adresselinje3,
                        postkode = utenlandsAdresseIFrittFormat.postkode,
                        byEllerStedsnavn = utenlandsAdresseIFrittFormat.byEllerStedsnavn,
                        landKode = utenlandsAdresseIFrittFormat.landkode,
                    ),
            )
            val postboksAdresseIFrittFormat = TestPersonBuilder.postadresseIFrittFormat()
            visitor.visitPostAdresseIFrittFormat(
                adresse =
                    PDLAdresse.PostAdresseIFrittFormat(
                        adresseMetadata = metadata,
                        adresseLinje1 = postboksAdresseIFrittFormat.adresselinje1,
                        adresseLinje2 = postboksAdresseIFrittFormat.adresselinje2,
                        adresseLinje3 = postboksAdresseIFrittFormat.adresselinje3,
                        postnummer = postboksAdresseIFrittFormat.postnummer,
                    ),
            )
            val postboksAdresse = TestPersonBuilder.postboksadresse()
            visitor.visitPostboksadresse(
                adresse =
                    PDLAdresse.PostboksAdresse(
                        adresseMetadata = metadata,
                        postbokseier = postboksAdresse.postbokseier,
                        postboks = postboksAdresse.postboks,
                        postnummer = postboksAdresse.postnummer,
                    ),
            )
            utenlandskAdresse(metadata, visitor)
            vegadresse(metadata, visitor)
        }
        confirmVerified(visitor)
    }

    @Test
    fun `aksepterer bosteds visitor`() {
        val visitor = mockk<BostedsAdresseVisitor>(relaxed = true)
        val metadata =
            AdresseMetadata(
                adresseType = AdresseMetadata.AdresseType.BOSTEDSADRESSE,
                gyldigFom = yesterday,
                gyldigTom = today,
                angittFlytteDato = yesterday,
                master = AdresseMetadata.MasterType.PDL,
                type = "Innland",
                coAdresseNavn = null,
            )

        val pdlPerson =
            TestPersonBuilder(
                bostedsAdresser =
                    listOf(
                        TestPersonBuilder.bostedsAdresse(
                            angittFlyttedato = yesterday,
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            matrikkeladresse = TestPersonBuilder.matrikkelAdresse(),
                        ),
                        TestPersonBuilder.bostedsAdresse(
                            angittFlyttedato = yesterday,
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            vegadresse = TestPersonBuilder.vegadresse(),
                        ),
                        TestPersonBuilder.bostedsAdresse(
                            angittFlyttedato = yesterday,
                            gyldigFom = yesterday,
                            gyldigTom = today,
                            utenlandskAdresse = TestPersonBuilder.utenlandskAdresse(),
                        ),
                    ),
            ).testPerson

        pdlPerson.acceptBostedsAdresseVisitor(visitor)
        verify {
            martikkelAdresse(metadata, visitor)
            vegadresse(metadata, visitor)
            utenlandskAdresse(metadata, visitor)
        }
        confirmVerified(visitor)
    }

    private fun utenlandskAdresse(
        metadata: AdresseMetadata,
        visitor: UtenlandskAdresseVisitor,
    ) {
        val utenlandskAdresse = TestPersonBuilder.utenlandskAdresse()
        visitor.visitUtenlandskAdresse(
            adresse =
                PDLAdresse.UtenlandskAdresse(
                    adresseMetadata = metadata,
                    adressenavnNummer = utenlandskAdresse.adressenavnNummer,
                    bySted = utenlandskAdresse.bySted,
                    bygningEtasjeLeilighet = utenlandskAdresse.bygningEtasjeLeilighet,
                    landKode = utenlandskAdresse.landkode,
                    postboksNummerNavn = utenlandskAdresse.postboksNummerNavn,
                    postkode = utenlandskAdresse.postkode,
                    regionDistriktOmraade = utenlandskAdresse.regionDistriktOmraade,
                ),
        )
    }

    private fun vegadresse(
        metadata: AdresseMetadata,
        visitor: VegAdresseVisitor,
    ) {
        val vegadresse = TestPersonBuilder.vegadresse()
        visitor.visitVegAdresse(
            adresse =
                PDLAdresse.VegAdresse(
                    adresseMetadata = metadata,
                    husbokstav = vegadresse.husbokstav,
                    husnummer = vegadresse.husnummer,
                    adressenavn = vegadresse.adressenavn,
                    bruksenhetsnummer = vegadresse.bruksenhetsnummer,
                    tilleggsnavn = vegadresse.tilleggsnavn,
                    postnummer = vegadresse.postnummer,
                    kommunenummer = vegadresse.kommunenummer,
                    bydelsnummer = vegadresse.bydelsnummer,
                ),
        )
    }

    private fun martikkelAdresse(
        metadata: AdresseMetadata,
        visitor: MatrikkelAdresseVisitor,
    ) {
        val matrikkelAdresse = TestPersonBuilder.matrikkelAdresse()
        visitor.visitMatrikkelAdresse(
            adresse =
                PDLAdresse.MatrikkelAdresse(
                    adresseMetadata = metadata,
                    bruksenhetsnummer = matrikkelAdresse.bruksenhetsnummer,
                    kommunenummer = matrikkelAdresse.kommunenummer,
                    postnummer = matrikkelAdresse.postnummer,
                    tilleggsnavn = matrikkelAdresse.tilleggsnavn,
                ),
        )
    }
}
