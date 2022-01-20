package no.nav.dagpenger.pdl

import no.nav.dagpenger.pdl.AdresseMetadata.Companion.from
import no.nav.pdl.personby.Bostedsadresse
import no.nav.pdl.personby.Matrikkeladresse
import no.nav.pdl.personby.Navn
import no.nav.pdl.personby.Oppholdsadresse
import no.nav.pdl.personby.Person
import no.nav.pdl.personby.UtenlandskAdresse
import no.nav.pdl.personby.Vegadresse
import no.nav.sbl.dialogarena.soknadinnsending.consumer.person.pdl.dto.PDLAdresse
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class PDLPerson(private val person: Person) {
    enum class Kjonn {
        MANN, KVINNE, UKJENT
    }

    enum class AdressebeskyttelseGradering {
        FORTROLIG, STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND, UGRADERT
    }

    class PDLException(msg: String?) : RuntimeException(msg)

    fun acceptPersonaliaVisitor(visitor: PersonaliaVisitor) {
        visitor.visit(
            fodselnummer,
            fodselsdato,
            alder,
            adresseBeskyttelse,
            fornavn,
            mellomnavn,
            etternavn,
            statsborgerskap,
            kjonn
        )
    }

    fun acceptKontaktAdresseVisitor(visitor: KontaktAdresseVisitor) {
        person.kontaktadresse.forEach { kontakAdresse ->
            val adresseMetadata = from(kontakAdresse)
            with(kontakAdresse) {
                vegadresse?.let { acceptVegAdresseVisitor(adresseMetadata, visitor, it) }
                utenlandskAdresse?.let { acceptUtenlandskAdresseVisitor(adresseMetadata, visitor, it) }
                postadresseIFrittFormat?.let {
                    visitor.visitPostAdresseIFrittFormat(
                        PDLAdresse.PostAdresseIFrittFormat(
                            adresseMetadata = adresseMetadata,
                            adresseLinje1 = it.adresselinje1,
                            adresseLinje2 = it.adresselinje2,
                            adresseLinje3 = it.adresselinje3,
                            postnummer = it.postnummer
                        )
                    )
                }
                postboksadresse?.let {
                    visitor.visitPostboksadresse(
                        PDLAdresse.PostboksAdresse(
                            adresseMetadata = adresseMetadata,
                            postbokseier = it.postbokseier,
                            postboks = it.postboks,
                            postnummer = it.postnummer
                        )
                    )
                }
                utenlandskAdresseIFrittFormat?.let {
                    visitor.visitUtenlandskAdresseIFrittFormat(
                        PDLAdresse.UtenlandsAdresseIFrittFormat(
                            adresseMetadata = adresseMetadata,
                            adresseLinje1 = it.adresselinje1,
                            adresseLinje2 = it.adresselinje2,
                            adresseLinje3 = it.adresselinje3,
                            postkode = it.postkode,
                            byEllerStedsnavn = it.byEllerStedsnavn,
                            landKode = it.landkode
                        )
                    )
                }
            }
        }
    }

    fun acceptOppholdsAdressseVisitor(visitor: OppholdsAdresseVisitor) {
        person.oppholdsadresse.forEach { oppholdsadresse: Oppholdsadresse ->
            val adresseMetadata = from(oppholdsadresse)
            with(oppholdsadresse) {
                vegadresse?.let { acceptVegAdresseVisitor(adresseMetadata, visitor, it) }
                matrikkeladresse?.let {
                    acceptMatrikkelAdresseVisitor(adresseMetadata, visitor, it)
                }
                utenlandskAdresse?.let {
                    acceptUtenlandskAdresseVisitor(adresseMetadata, visitor, it)
                }
            }
        }
    }

    fun acceptBostedsAdresseVisitor(visitor: BostedsAdresseVisitor) {
        person.bostedsadresse.forEach { bostedsadresse: Bostedsadresse ->
            val adresseMetadata = from(bostedsadresse)
            with(bostedsadresse) {
                vegadresse?.let { acceptVegAdresseVisitor(adresseMetadata, visitor, it) }
                matrikkeladresse?.let {
                    acceptMatrikkelAdresseVisitor(
                        adresseMetadata, visitor, it
                    )
                }
                utenlandskAdresse?.let {
                    acceptUtenlandskAdresseVisitor(adresseMetadata, visitor, it)
                }
                ukjentBosted?.let {
                    visitor.visitUkjentBosted(
                        adresseMetadata,
                        it.bostedskommune
                    )
                }
            }
        }
    }

    private fun acceptUtenlandskAdresseVisitor(
        adresseMetadata: AdresseMetadata,
        visitor: UtenlandskAdresseVisitor,
        utenlandskAdresse: UtenlandskAdresse
    ) {
        visitor.visitUtenlandskAdresse(
            PDLAdresse.UtenlandskAdresse(
                adresseMetadata = adresseMetadata,
                adressenavnNummer = utenlandskAdresse.adressenavnNummer,
                bySted = utenlandskAdresse.bySted,
                bygningEtasjeLeilighet = utenlandskAdresse.bygningEtasjeLeilighet,
                landKode = utenlandskAdresse.landkode,
                postboksNummerNavn = utenlandskAdresse.postboksNummerNavn,
                postkode = utenlandskAdresse.postkode,
                regionDistriktOmraade = utenlandskAdresse.regionDistriktOmraade
            )
        )
    }

    private fun acceptMatrikkelAdresseVisitor(
        adresseMetadata: AdresseMetadata,
        visitor: MatrikkelAdresseVisitor,
        matrikkeladresse: Matrikkeladresse
    ) {
        visitor.visitMatrikkelAdresse(
            PDLAdresse.MatrikkelAdresse(
                adresseMetadata = adresseMetadata,
                bruksenhetsnummer = matrikkeladresse.bruksenhetsnummer,
                kommunenummer = matrikkeladresse.kommunenummer,
                matrikkelId = matrikkeladresse.matrikkelId,
                postnummer = matrikkeladresse.postnummer,
                tilleggsnavn = matrikkeladresse.tilleggsnavn
            )
        )
    }

    private fun acceptVegAdresseVisitor(
        adresseMetadata: AdresseMetadata,
        visitor: VegAdresseVisitor,
        vegadresse: Vegadresse
    ) {
        visitor.visitVegAdresse(
            PDLAdresse.VegAdresse(
                adresseMetadata = adresseMetadata,
                adressenavn = vegadresse.adressenavn,
                bruksenhetsnummer = vegadresse.bruksenhetsnummer,
                bydelsnummer = vegadresse.bydelsnummer,
                husbokstav = vegadresse.husbokstav,
                husnummer = vegadresse.husnummer,
                kommunenummer = vegadresse.kommunenummer,
                postnummer = vegadresse.postnummer,
                tilleggsnavn = vegadresse.tilleggsnavn
            )
        )
    }

    private val fodselnummer: String = person.folkeregisteridentifikator.firstOrNull()?.identifikasjonsnummer
        ?: throw PDLException("Ingen fodselsnummer funnet")

    private val fodselsdato: LocalDate =
        person.foedsel.firstOrNull()?.foedselsdato ?: throw PDLException("Ingen fodselsdato funnet")

    private val alder: Long = ChronoUnit.YEARS.between(fodselsdato, LocalDate.now())

    private val adresseBeskyttelse: AdressebeskyttelseGradering = person.adressebeskyttelse.firstOrNull()?.let {
        AdressebeskyttelseGradering.valueOf(it.gradering.name)
    } ?: AdressebeskyttelseGradering.UGRADERT

    private val navn: Navn = person.navn.firstOrNull() ?: throw PDLException("Ingen navn funnet")
    private val fornavn: String = navn.fornavn
    private val mellomnavn: String? = navn.mellomnavn
    private val etternavn: String = navn.etternavn
    private val statsborgerskap: String = person.statsborgerskap.firstOrNull { it.land == "NOR" }?.land
        ?: person.statsborgerskap.firstOrNull()?.land ?: throw PDLException("Ingen statsborgerskap funnet")

    private val kjonn: Kjonn = person.kjoenn.firstOrNull()?.kjoenn?.let { Kjonn.valueOf(it.toString()) }
        ?: Kjonn.UKJENT
}
