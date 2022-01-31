package no.nav.dagpenger.pdl

import no.nav.pdl.enums.AdressebeskyttelseGradering
import no.nav.pdl.enums.KjoennType
import no.nav.pdl.enums.KontaktadresseType
import no.nav.pdl.personby.Adressebeskyttelse
import no.nav.pdl.personby.Bostedsadresse
import no.nav.pdl.personby.Foedsel
import no.nav.pdl.personby.Folkeregisteridentifikator
import no.nav.pdl.personby.Kjoenn
import no.nav.pdl.personby.Kontaktadresse
import no.nav.pdl.personby.Matrikkeladresse
import no.nav.pdl.personby.Navn
import no.nav.pdl.personby.Oppholdsadresse
import no.nav.pdl.personby.Person
import no.nav.pdl.personby.PostadresseIFrittFormat
import no.nav.pdl.personby.Postboksadresse
import no.nav.pdl.personby.Statsborgerskap
import no.nav.pdl.personby.UtenlandskAdresse
import no.nav.pdl.personby.UtenlandskAdresseIFrittFormat
import no.nav.pdl.personby.Vegadresse
import java.time.LocalDate

internal data class TestPersonBuilder(
    val fnr: String = "31122312345",
    val fodselsdato: LocalDate = LocalDate.of(1980, 12, 23),
    val fornavn: String = "person",
    val mellomnavn: String = "mellomnavn",
    val etternavn: String = "mock",
    val statsborgerskap: List<String> = listOf("NOR", "POL"),
    var adressebeskyttelseGradering: String = "UGRADERT",
    val bostedsAdresser: List<Bostedsadresse> = ArrayList(),
    val kontaktAdresser: List<Kontaktadresse> = ArrayList(),
    val oppholdAdresser: List<Oppholdsadresse> = ArrayList(),
    val kjonn: String = "KVINNE"

) {

    val testPerson = Person(
        folkeregisteridentifikator = listOf(
            Folkeregisteridentifikator(
                identifikasjonsnummer = fnr,
                status = "",
                type = ""
            )
        ),
        forelderBarnRelasjon = listOf(),
        foedsel = listOf(
            Foedsel(fodselsdato)
        ),
        navn = listOf(
            Navn(
                etternavn = etternavn,
                fornavn = fornavn,
                gyldigFraOgMed = null,
                mellomnavn = mellomnavn
            )
        ),
        statsborgerskap = statsborgerskap.map {
            Statsborgerskap(it)
        },
        kjoenn = listOf(
            Kjoenn(kjoenn = KjoennType.valueOf(kjonn))
        ),
        adressebeskyttelse = listOf(
            Adressebeskyttelse(gradering = AdressebeskyttelseGradering.valueOf(adressebeskyttelseGradering))
        ),
        kontaktadresse = kontaktAdresser,
        oppholdsadresse = oppholdAdresser,
        bostedsadresse = bostedsAdresser,
        doedsfall = emptyList()
    )

    companion object {
        fun bostedsAdresse(
            angittFlyttedato: LocalDate? = null,
            master: String = "PDL",
            gyldigFom: LocalDate? = null,
            gyldigTom: LocalDate? = null,
            vegadresse: Vegadresse? = null,
            matrikkeladresse: Matrikkeladresse? = null,
            utenlandskAdresse: UtenlandskAdresse? = null
        ): Bostedsadresse {
            return Bostedsadresse(
                angittFlyttedato = angittFlyttedato,
                coAdressenavn = null,
                folkeregistermetadata = null,
                gyldigFraOgMed = gyldigFom?.atStartOfDay(),
                gyldigTilOgMed = gyldigTom?.atStartOfDay(),
                matrikkeladresse = matrikkeladresse,
                metadata = no.nav.pdl.personby.Metadata(emptyList(), master, "", false),
                ukjentBosted = null,
                utenlandskAdresse = utenlandskAdresse,
                vegadresse = vegadresse,
            )
        }

        fun kontaktAdresse(
            master: String = "PDL",
            gyldigFom: LocalDate? = null,
            gyldigTom: LocalDate? = null,
            utenlandskAdresseIFrittFormat: UtenlandskAdresseIFrittFormat? = null,
            utenlandskAdresse: UtenlandskAdresse? = null,
            postadresseIFrittFormat: PostadresseIFrittFormat? = null,
            postboksadresse: Postboksadresse? = null,
            vegadresse: Vegadresse? = null
        ): Kontaktadresse {
            return Kontaktadresse(
                coAdressenavn = null,
                folkeregistermetadata = null,
                gyldigFraOgMed = gyldigFom?.atStartOfDay(),
                gyldigTilOgMed = gyldigTom?.atStartOfDay(),
                metadata = no.nav.pdl.personby.Metadata(emptyList(), master, "", false),
                postadresseIFrittFormat = postadresseIFrittFormat,
                postboksadresse = postboksadresse,
                type = KontaktadresseType.INNLAND,
                utenlandskAdresse = utenlandskAdresse,
                utenlandskAdresseIFrittFormat = utenlandskAdresseIFrittFormat,
                vegadresse = vegadresse
            )
        }

        fun oppholdsAdresse(
            master: String = "PDL",
            gyldigFom: LocalDate? = null,
            gyldigTom: LocalDate? = null,
            vegadresse: Vegadresse? = null,
            matrikkeladresse: Matrikkeladresse? = null,
            utenlandskAdresse: UtenlandskAdresse? = null
        ): Oppholdsadresse {
            return Oppholdsadresse(
                coAdressenavn = null,
                folkeregistermetadata = null,
                gyldigFraOgMed = gyldigFom?.atStartOfDay(),
                gyldigTilOgMed = gyldigTom?.atStartOfDay(),
                matrikkeladresse = matrikkeladresse,
                metadata = no.nav.pdl.personby.Metadata(emptyList(), master, "", false),
                oppholdAnnetSted = "",
                utenlandskAdresse = utenlandskAdresse,
                vegadresse = vegadresse
            )
        }

        fun vegadresse(
            husbokstav: String? = "husbokstav",
            husnummer: String? = "husnummer",
            adressenavn: String? = "adressenavn",
            bruksenhetsnummer: String? = "bruksenhetsnummer",
            tilleggsnavn: String? = "tilleggsnavn",
            postnummer: String? = "postnummer",
            kommunenummer: String? = "kommunenummer",
            bydelsnummer: String? = "bydelsnummer",

        ): Vegadresse {
            return Vegadresse(
                matrikkelId = null,
                husbokstav = husbokstav,
                husnummer = husnummer,
                adressenavn = adressenavn,
                bruksenhetsnummer = bruksenhetsnummer,
                tilleggsnavn = tilleggsnavn,
                postnummer = postnummer,
                kommunenummer = kommunenummer,
                bydelsnummer = bydelsnummer,
                koordinater = null
            )
        }

        fun utenlandskAdresseIFrittFormat(
            adresselinje1: String? = "adresselinje1",
            adresselinje2: String? = "adresselinje2",
            adresselinje3: String? = "adresselinje3",
            byEllerStedsnavn: String? = "byEllerStedsnavn",
            landkode: String = "NOR",
            postkode: String? = "2013"
        ): UtenlandskAdresseIFrittFormat {
            return UtenlandskAdresseIFrittFormat(
                adresselinje1 = adresselinje1,
                adresselinje2 = adresselinje2,
                adresselinje3 = adresselinje3,
                byEllerStedsnavn = byEllerStedsnavn,
                landkode = landkode,
                postkode = postkode
            )
        }

        fun matrikkelAdresse(
            bruksenhetsnummer: String? = "bruksenhetsnummer",
            tilleggsnavn: String? = "tilleggsnavn",
            postnummer: String? = "postnummer",
            kommunenummer: String? = "kommunenummer",
        ): Matrikkeladresse {
            return Matrikkeladresse(
                matrikkelId = null,
                bruksenhetsnummer = bruksenhetsnummer,
                tilleggsnavn = tilleggsnavn,
                postnummer = postnummer,
                kommunenummer = kommunenummer,
                koordinater = null
            )
        }

        fun postadresseIFrittFormat(
            adresselinje1: String? = "adresselinje1",
            adresselinje2: String? = "adresselinje2",
            adresselinje3: String? = "adresselinje3",
            postnummer: String? = "postnummer"
        ): PostadresseIFrittFormat {
            return PostadresseIFrittFormat(
                adresselinje1 = adresselinje1,
                adresselinje2 = adresselinje2,
                adresselinje3 = adresselinje3,
                postnummer = postnummer
            )
        }

        fun postboksadresse(
            postboks: String = "postboks",
            postbokseier: String? = "postbokseier",
            postnummer: String? = "postnummer"
        ): Postboksadresse {
            return Postboksadresse(
                postboks = postboks,
                postbokseier = postbokseier,
                postnummer = postnummer
            )
        }

        fun utenlandskAdresse(
            adressenavnNummer: String? = "adressenavnNummer",
            bygningEtasjeLeilighet: String? = "bygningEtasjeLeilighet",
            postboksNummerNavn: String? = "postboksNummerNavn",
            postkode: String? = "postkode",
            bySted: String? = "bySted",
            regionDistriktOmraade: String? = "regionDistriktOmraade",
            landkode: String = "NOR"
        ): UtenlandskAdresse {
            return UtenlandskAdresse(
                adressenavnNummer = adressenavnNummer,
                bygningEtasjeLeilighet = bygningEtasjeLeilighet,
                postboksNummerNavn = postboksNummerNavn,
                postkode = postkode,
                bySted = bySted,
                regionDistriktOmraade = regionDistriktOmraade,
                landkode = landkode
            )
        }
    }

    val pdlPerson: PDLPerson
        get() = PDLPerson(testPerson)
}
