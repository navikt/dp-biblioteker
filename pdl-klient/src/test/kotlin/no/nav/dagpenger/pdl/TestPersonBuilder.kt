@file:Suppress("ktlint:standard:function-naming")

package no.nav.dagpenger.pdl

import no.nav.dagpenger.pdl.dto.AdressebeskyttelseGradering
import no.nav.dagpenger.pdl.dto.KjoennType
import no.nav.dagpenger.pdl.dto.KontaktadresseType
import no.nav.dagpenger.pdl.entity.Adressebeskyttelse
import no.nav.dagpenger.pdl.entity.AdressebeskyttelseProjection
import no.nav.dagpenger.pdl.entity.Bostedsadresse
import no.nav.dagpenger.pdl.entity.BostedsadresseProjection
import no.nav.dagpenger.pdl.entity.DeltBosted
import no.nav.dagpenger.pdl.entity.DoedfoedtBarn
import no.nav.dagpenger.pdl.entity.Doedsfall
import no.nav.dagpenger.pdl.entity.Endring
import no.nav.dagpenger.pdl.entity.FalskIdentitet
import no.nav.dagpenger.pdl.entity.Foedested
import no.nav.dagpenger.pdl.entity.Foedsel
import no.nav.dagpenger.pdl.entity.Foedselsdato
import no.nav.dagpenger.pdl.entity.FoedselsdatoProjection
import no.nav.dagpenger.pdl.entity.Folkeregisteridentifikator
import no.nav.dagpenger.pdl.entity.FolkeregisteridentifikatorProjection
import no.nav.dagpenger.pdl.entity.Folkeregistermetadata
import no.nav.dagpenger.pdl.entity.FolkeregistermetadataProjection
import no.nav.dagpenger.pdl.entity.Folkeregisterpersonstatus
import no.nav.dagpenger.pdl.entity.ForelderBarnRelasjon
import no.nav.dagpenger.pdl.entity.Foreldreansvar
import no.nav.dagpenger.pdl.entity.Identitetsgrunnlag
import no.nav.dagpenger.pdl.entity.InnflyttingTilNorge
import no.nav.dagpenger.pdl.entity.Kjoenn
import no.nav.dagpenger.pdl.entity.KjoennProjection
import no.nav.dagpenger.pdl.entity.Kontaktadresse
import no.nav.dagpenger.pdl.entity.KontaktadresseProjection
import no.nav.dagpenger.pdl.entity.KontaktinformasjonForDoedsbo
import no.nav.dagpenger.pdl.entity.Koordinater
import no.nav.dagpenger.pdl.entity.Matrikkeladresse
import no.nav.dagpenger.pdl.entity.MatrikkeladresseProjection
import no.nav.dagpenger.pdl.entity.Metadata
import no.nav.dagpenger.pdl.entity.MetadataProjection
import no.nav.dagpenger.pdl.entity.Navn
import no.nav.dagpenger.pdl.entity.NavnProjection
import no.nav.dagpenger.pdl.entity.Navspersonidentifikator
import no.nav.dagpenger.pdl.entity.Opphold
import no.nav.dagpenger.pdl.entity.Oppholdsadresse
import no.nav.dagpenger.pdl.entity.OppholdsadresseProjection
import no.nav.dagpenger.pdl.entity.OriginaltNavn
import no.nav.dagpenger.pdl.entity.Person
import no.nav.dagpenger.pdl.entity.PersonProjection
import no.nav.dagpenger.pdl.entity.PostadresseIFrittFormat
import no.nav.dagpenger.pdl.entity.PostadresseIFrittFormatProjection
import no.nav.dagpenger.pdl.entity.Postboksadresse
import no.nav.dagpenger.pdl.entity.PostboksadresseProjection
import no.nav.dagpenger.pdl.entity.RettsligHandleevne
import no.nav.dagpenger.pdl.entity.Sikkerhetstiltak
import no.nav.dagpenger.pdl.entity.Sivilstand
import no.nav.dagpenger.pdl.entity.Statsborgerskap
import no.nav.dagpenger.pdl.entity.StatsborgerskapProjection
import no.nav.dagpenger.pdl.entity.Telefonnummer
import no.nav.dagpenger.pdl.entity.TilrettelagtKommunikasjon
import no.nav.dagpenger.pdl.entity.UkjentBosted
import no.nav.dagpenger.pdl.entity.UtenlandskAdresse
import no.nav.dagpenger.pdl.entity.UtenlandskAdresseIFrittFormat
import no.nav.dagpenger.pdl.entity.UtenlandskAdresseIFrittFormatProjection
import no.nav.dagpenger.pdl.entity.UtenlandskAdresseProjection
import no.nav.dagpenger.pdl.entity.UtenlandskIdentifikasjonsnummer
import no.nav.dagpenger.pdl.entity.UtflyttingFraNorge
import no.nav.dagpenger.pdl.entity.Vegadresse
import no.nav.dagpenger.pdl.entity.VegadresseProjection
import no.nav.dagpenger.pdl.entity.VergemaalEllerFremtidsfullmakt
import java.time.LocalDate
import java.time.LocalDateTime

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
    val kjonn: String = "KVINNE",
) {
    val testPerson =
        PDLPerson(
            object : Person {
                override val adressebeskyttelse: List<Adressebeskyttelse> =
                    listOf(
                        object : Adressebeskyttelse {
                            override val folkeregistermetadata: Folkeregistermetadata? = null
                            override val gradering: AdressebeskyttelseGradering =
                                AdressebeskyttelseGradering.valueOf(adressebeskyttelseGradering)
                            override val metadata: Metadata = metadata("PDL")

                            override fun AdressebeskyttelseProjection.__withCurrentProjection() {}
                        },
                    )
                override val bostedsadresse: List<Bostedsadresse> = bostedsAdresser
                override val deltBosted: List<DeltBosted> = emptyList()
                override val doedfoedtBarn: List<DoedfoedtBarn> = emptyList()
                override val doedsfall: List<Doedsfall> = emptyList()
                override val falskIdentitet: FalskIdentitet? = null
                override val foedsel: List<Foedsel> = emptyList()
                override val foedselsdato: List<Foedselsdato> = listOf(foedseldato(fodselsdato))
                override val foedested: List<Foedested> = emptyList()
                override val navspersonidentifikator: List<Navspersonidentifikator> = emptyList()
                override val folkeregisteridentifikator: List<Folkeregisteridentifikator> =
                    listOf(folkeregisteridentifikator(id = fnr))
                override val folkeregisterpersonstatus: List<Folkeregisterpersonstatus> = emptyList()
                override val forelderBarnRelasjon: List<ForelderBarnRelasjon> = emptyList()
                override val foreldreansvar: List<Foreldreansvar> = emptyList()
                override val identitetsgrunnlag: List<Identitetsgrunnlag> = emptyList()
                override val innflyttingTilNorge: List<InnflyttingTilNorge> = emptyList()
                override val kjoenn: List<Kjoenn> = listOf(kjonn(kjonn))
                override val kontaktadresse: List<Kontaktadresse> = kontaktAdresser
                override val kontaktinformasjonForDoedsbo: List<KontaktinformasjonForDoedsbo> = emptyList()
                override val navn: List<Navn> = listOf(navn(fornavn, mellomnavn, etternavn))
                override val opphold: List<Opphold> = emptyList()
                override val oppholdsadresse: List<Oppholdsadresse> = oppholdAdresser
                override val sikkerhetstiltak: List<Sikkerhetstiltak> = emptyList()
                override val sivilstand: List<Sivilstand> = emptyList()
                override val statsborgerskap: List<Statsborgerskap> = listOf(statsborgerskap("NOR"))
                override val telefonnummer: List<Telefonnummer> = emptyList()
                override val tilrettelagtKommunikasjon: List<TilrettelagtKommunikasjon> = emptyList()
                override val utenlandskIdentifikasjonsnummer: List<UtenlandskIdentifikasjonsnummer> = emptyList()
                override val utflyttingFraNorge: List<UtflyttingFraNorge> = emptyList()
                override val vergemaalEllerFremtidsfullmakt: List<VergemaalEllerFremtidsfullmakt> = emptyList()
                override val rettsligHandleevne: List<RettsligHandleevne>
                    get() = TODO("Not yet implemented")

                override fun PersonProjection.__withCurrentProjection() {
                    TODO("Not yet implemented")
                }
            },
        )

    companion object {
        fun postboksadresse(
            postboks: String = "postboks",
            postbokseier: String? = null,
            postnummer: String? = null,
        ): Postboksadresse =
            object : Postboksadresse {
                override val postboks: String = postboks
                override val postbokseier: String? = postbokseier
                override val postnummer: String? = postnummer

                override fun PostboksadresseProjection.__withCurrentProjection() {}
            }

        fun postadresseIFrittFormat(
            adresselinje1: String? = null,
            adresselinje2: String? = null,
            adresselinje3: String? = null,
            postnummer: String? = null,
        ): PostadresseIFrittFormat =
            object : PostadresseIFrittFormat {
                override val adresselinje1: String? = adresselinje1
                override val adresselinje2: String? = adresselinje2
                override val adresselinje3: String? = adresselinje3
                override val postnummer: String? = postnummer

                override fun PostadresseIFrittFormatProjection.__withCurrentProjection() {}
            }

        fun utenlandskAdresse(
            adressenavnNummer: String? = null,
            bySted: String? = null,
            bygningEtasjeLeilighet: String? = null,
            landkode: String = "NOR",
            postboksNummerNavn: String? = null,
            postkode: String? = null,
            regionDistriktOmraade: String? = null,
        ): UtenlandskAdresse =
            object : UtenlandskAdresse {
                override val adressenavnNummer: String? = adressenavnNummer
                override val bySted: String? = bySted
                override val bygningEtasjeLeilighet: String? = bygningEtasjeLeilighet
                override val landkode: String = landkode
                override val postboksNummerNavn: String? = postboksNummerNavn
                override val postkode: String? = postkode
                override val regionDistriktOmraade: String? = regionDistriktOmraade

                override fun UtenlandskAdresseProjection.__withCurrentProjection() {}
            }

        fun matrikkelAdresse(
            bruksenhetsnummer: String? = null,
            kommunenummer: String? = null,
            matrikkelId: Long? = null,
            postnummer: String? = null,
            tillegsnavn: String? = null,
        ): Matrikkeladresse =
            object : Matrikkeladresse {
                override val bruksenhetsnummer: String? = bruksenhetsnummer
                override val kommunenummer: String? = kommunenummer
                override val koordinater: Koordinater? = null
                override val matrikkelId: Long? = matrikkelId
                override val postnummer: String? = postnummer
                override val tilleggsnavn: String? = tillegsnavn

                override fun MatrikkeladresseProjection.__withCurrentProjection() {}
            }

        fun utenlandskAdresseIFrittFormat(
            adresselinje1: String? = null,
            adresselinje2: String? = null,
            adresselinje3: String? = null,
            byEllerStedsnavn: String? = null,
            landkode: String = "NOR",
            postkode: String? = null,
        ): UtenlandskAdresseIFrittFormat =

            object : UtenlandskAdresseIFrittFormat {
                override val adresselinje1: String? = adresselinje1
                override val adresselinje2: String? = adresselinje2
                override val adresselinje3: String? = adresselinje3
                override val byEllerStedsnavn: String? = byEllerStedsnavn
                override val landkode: String = landkode
                override val postkode: String? = postkode

                override fun UtenlandskAdresseIFrittFormatProjection.__withCurrentProjection() {}
            }

        fun foedseldato(foedselsdato: LocalDate): Foedselsdato =
            object : Foedselsdato {
                override val foedselsdato: LocalDate = foedselsdato
                override val foedselsaar: Int? = null
                override val folkeregistermetadata: Folkeregistermetadata? = null
                override val metadata: Metadata = metadata()

                override fun FoedselsdatoProjection.__withCurrentProjection() {}
            }

        fun statsborgerskap(landKode: String): Statsborgerskap =
            object : Statsborgerskap {
                override val bekreftelsesdato: LocalDate? = null
                override val folkeregistermetadata: Folkeregistermetadata? = null
                override val gyldigFraOgMed: LocalDate? = null
                override val gyldigTilOgMed: LocalDate? = null
                override val land: String = landKode
                override val metadata: Metadata = metadata()

                override fun StatsborgerskapProjection.__withCurrentProjection() {}
            }

        fun navn(
            fornavn: String,
            mellomnavn: String? = null,
            etternavn: String,
        ): Navn =
            object : Navn {
                override val etternavn: String = etternavn
                override val folkeregistermetadata: Folkeregistermetadata? = null
                override val forkortetNavn: String? = null
                override val fornavn: String = fornavn
                override val gyldigFraOgMed: LocalDate? = null
                override val mellomnavn: String? = mellomnavn
                override val metadata: Metadata = metadata()
                override val originaltNavn: OriginaltNavn? = null

                override fun NavnProjection.__withCurrentProjection() {}
            }

        fun kjonn(kjonn: String): Kjoenn =
            object : Kjoenn {
                override val folkeregistermetadata: Folkeregistermetadata? = null
                override val kjoenn: KjoennType? = KjoennType.valueOf(kjonn)
                override val metadata: Metadata = metadata()

                override fun KjoennProjection.__withCurrentProjection() {}
            }

        fun folkeregisteridentifikator(
            id: String,
            status: String = "I_BRUK",
            type: String = "FNR",
        ): Folkeregisteridentifikator =
            object : Folkeregisteridentifikator {
                override val folkeregistermetadata: Folkeregistermetadata = folkeregistermetadata()
                override val identifikasjonsnummer: String = id
                override val metadata: Metadata = metadata()
                override val status: String = status
                override val type: String = type

                override fun FolkeregisteridentifikatorProjection.__withCurrentProjection() {}
            }

        fun folkeregistermetadata(): Folkeregistermetadata {
            return object : Folkeregistermetadata {
                override val aarsak: String? = null
                override val ajourholdstidspunkt: LocalDateTime? = null
                override val gyldighetstidspunkt: LocalDateTime? = null
                override val kilde: String? = null
                override val opphoerstidspunkt: LocalDateTime? = null
                override val sekvens: Int? = null

                override fun FolkeregistermetadataProjection.__withCurrentProjection() {}
            }
        }

        fun oppholdsAdresse(
            master: String = "PDL",
            gyldigFom: LocalDate? = null,
            gyldigTom: LocalDate? = null,
            vegadresse: Vegadresse? = null,
            matrikkeladresse: Matrikkeladresse? = null,
            utenlandskAdresse: UtenlandskAdresse? = null,
        ): Oppholdsadresse {
            return object : Oppholdsadresse {
                override val coAdressenavn: String? = null
                override val folkeregistermetadata: Folkeregistermetadata? = null
                override val gyldigFraOgMed: LocalDateTime? = gyldigFom?.atStartOfDay()
                override val gyldigTilOgMed: LocalDateTime? = gyldigTom?.atStartOfDay()
                override val matrikkeladresse: Matrikkeladresse? = matrikkeladresse
                override val metadata: Metadata = metadata(master)
                override val oppholdAnnetSted: String? = null
                override val utenlandskAdresse: UtenlandskAdresse? = utenlandskAdresse
                override val vegadresse: Vegadresse? = vegadresse

                override fun OppholdsadresseProjection.__withCurrentProjection() {}
            }
        }

        fun kontaktAdresse(
            master: String = "PDL",
            gyldigFom: LocalDate? = null,
            gyldigTom: LocalDate? = null,
            utenlandskAdresseIFrittFormat: UtenlandskAdresseIFrittFormat? = null,
            utenlandskAdresse: UtenlandskAdresse? = null,
            postadresseIFrittFormat: PostadresseIFrittFormat? = null,
            postboksadresse: Postboksadresse? = null,
            vegadresse: Vegadresse? = null,
        ): Kontaktadresse {
            return object : Kontaktadresse {
                override val coAdressenavn: String? = null
                override val folkeregistermetadata: Folkeregistermetadata? = null
                override val gyldigFraOgMed: LocalDateTime? = gyldigFom?.atStartOfDay()
                override val gyldigTilOgMed: LocalDateTime? = gyldigTom?.atStartOfDay()
                override val metadata: Metadata = metadata(master)
                override val postadresseIFrittFormat: PostadresseIFrittFormat? = postadresseIFrittFormat
                override val postboksadresse: Postboksadresse? = postboksadresse
                override val type: KontaktadresseType = KontaktadresseType.Innland
                override val utenlandskAdresse: UtenlandskAdresse? = utenlandskAdresse
                override val utenlandskAdresseIFrittFormat: UtenlandskAdresseIFrittFormat? =
                    utenlandskAdresseIFrittFormat
                override val vegadresse: Vegadresse? = vegadresse

                override fun KontaktadresseProjection.__withCurrentProjection() {}
            }
        }

        fun metadata(master: String = "PDL"): Metadata =
            object : Metadata {
                override val endringer: List<Endring> = emptyList()
                override val historisk: Boolean = false
                override val master: String = master
                override val opplysningsId: String? = null

                override fun MetadataProjection.__withCurrentProjection() {}
            }

        fun bostedsAdresse(
            master: String = "PDL",
            gyldigFom: LocalDate? = null,
            gyldigTom: LocalDate? = null,
            angittFlyttedato: LocalDate? = null,
            vegadresse: Vegadresse? = null,
            matrikkeladresse: Matrikkeladresse? = null,
            utenlandskAdresse: UtenlandskAdresse? = null,
        ): Bostedsadresse {
            return object : Bostedsadresse {
                override val angittFlyttedato: LocalDate? = angittFlyttedato
                override val coAdressenavn: String? = null
                override val folkeregistermetadata: Folkeregistermetadata? = null
                override val gyldigFraOgMed: LocalDateTime? = gyldigFom?.atStartOfDay()
                override val gyldigTilOgMed: LocalDateTime? = gyldigTom?.atStartOfDay()
                override val matrikkeladresse: Matrikkeladresse? = matrikkeladresse
                override val metadata: Metadata = metadata(master)
                override val ukjentBosted: UkjentBosted? = null
                override val utenlandskAdresse: UtenlandskAdresse? = utenlandskAdresse
                override val vegadresse: Vegadresse? = vegadresse

                override fun BostedsadresseProjection.__withCurrentProjection() {
                    TODO("Not yet implemented")
                }
            }
        }

        fun vegadresse(
            adressenavn: String? = null,
            bruksenhetsnummer: String? = null,
            bydelsnummer: String? = null,
            husbokstav: String? = null,
            husnummer: String? = null,
            kommunenummer: String? = null,
            postnummer: String? = null,
            tillegsnavn: String? = null,
        ): Vegadresse =
            object : Vegadresse {
                override val adressenavn: String? = adressenavn
                override val bruksenhetsnummer: String? = bruksenhetsnummer
                override val bydelsnummer: String? = bydelsnummer
                override val husbokstav: String? = husbokstav
                override val husnummer: String? = husnummer
                override val kommunenummer: String? = kommunenummer
                override val koordinater: Koordinater? = null
                override val matrikkelId: Long? = null
                override val postnummer: String? = postnummer
                override val tilleggsnavn: String? = tillegsnavn

                override fun VegadresseProjection.__withCurrentProjection() {}
            }
    }
}
