package no.nav.dagpenger.pdl.queries

import no.nav.dagpenger.pdl.dto.IdentGruppe
import no.nav.dagpenger.pdl.entity.IdentlisteProjection
import no.nav.dagpenger.pdl.entity.MatrikkeladresseProjection
import no.nav.dagpenger.pdl.entity.PersonProjection
import no.nav.dagpenger.pdl.entity.QueryProjection
import no.nav.dagpenger.pdl.entity.RelatertBiPersonProjection
import no.nav.dagpenger.pdl.entity.UtenlandskAdresseProjection
import no.nav.dagpenger.pdl.entity.VegadresseProjection

fun QueryProjection.hentPersonBolk(fnrs: List<String>) {
    hentPersonBolk(fnrs) {
        person {
            personDetailsFragment()
        }
    }
}

fun QueryProjection.hentPerson(fnr: String) {
    hentPerson(fnr) {
        personDetailsFragment()
    }
}

fun QueryProjection.hentIdenter(
    ident: String,
    grupper: List<IdentGruppe>? = null,
    historikk: Boolean? = null,
) {
    hentIdenter(ident, grupper, historikk) {
        identerDetailsFragment()
    }
}

fun MatrikkeladresseProjection.matrikkeladresseDetailsFragment() {
    matrikkelId()
    bruksenhetsnummer()
    tilleggsnavn()
    postnummer()
    kommunenummer()
}

fun VegadresseProjection.vegadresseDetailsFragment() {
    matrikkelId()
    husbokstav()
    husnummer()
    adressenavn()
    bruksenhetsnummer()
    tilleggsnavn()
    postnummer()
    kommunenummer()
    bydelsnummer()
}

fun UtenlandskAdresseProjection.utenlandskAdresseDetailsFragment() {
    adressenavnNummer()
    bygningEtasjeLeilighet()
    postboksNummerNavn()
    postkode()
    bySted()
    regionDistriktOmraade()
    landkode()
}

fun RelatertBiPersonProjection.relatertBiPersonFragment() {
    navn {
        fornavn()
        mellomnavn()
        etternavn()
    }
    foedselsdato()
    statsborgerskap()
    kjoenn()
}

fun PersonProjection.personDetailsFragment() {
    folkeregisteridentifikator {
        identifikasjonsnummer()
        status()
        type()
    }
    forelderBarnRelasjon {
        minRolleForPerson()
        relatertPersonsIdent()
        relatertPersonsRolle()
        relatertPersonUtenFolkeregisteridentifikator {
            relatertBiPersonFragment()
        }
    }
    doedsfall {
        doedsdato()
    }
    foedselsdato {
        foedselsdato()
    }
    navn {
        etternavn()
        fornavn()
        gyldigFraOgMed()
        mellomnavn()
    }
    statsborgerskap {
        land()
    }
    kjoenn {
        kjoenn()
    }
    adressebeskyttelse {
        gradering()
    }
    kontaktadresse {
        coAdressenavn()
        gyldigFraOgMed()
        gyldigTilOgMed()
        type()
        metadata {
            master()
        }
        postadresseIFrittFormat {
            adresselinje1()
            adresselinje2()
            adresselinje3()
            postnummer()
        }

        postboksadresse {
            postboks()
            postbokseier()
            postnummer()
        }

        utenlandskAdresse {
            utenlandskAdresseDetailsFragment()
        }
        utenlandskAdresseIFrittFormat {
            adresselinje1()
            adresselinje2()
            adresselinje3()
            byEllerStedsnavn()
            landkode()
            postkode()
        }
        vegadresse {
            vegadresseDetailsFragment()
        }
    }
    oppholdsadresse {
        coAdressenavn()
        gyldigFraOgMed()
        gyldigTilOgMed()
        metadata {
            master()
        }
        matrikkeladresse {
            matrikkeladresseDetailsFragment()
        }
        utenlandskAdresse {
            utenlandskAdresseDetailsFragment()
        }
        vegadresse {
            vegadresseDetailsFragment()
        }
    }
    bostedsadresse {
        angittFlyttedato()
        coAdressenavn()
        gyldigFraOgMed()
        gyldigTilOgMed()
        metadata {
            master()
        }
        matrikkeladresse {
            matrikkeladresseDetailsFragment()
        }
        ukjentBosted {
            bostedskommune()
        }
        vegadresse {
            vegadresseDetailsFragment()
        }
        utenlandskAdresse {
            utenlandskAdresseDetailsFragment()
        }
    }

    sikkerhetstiltak {
        tiltakstype()
        beskrivelse()
        kontaktperson {
            personident()
            enhet()
        }
        gyldigFraOgMed()
        gyldigTilOgMed()
    }
}

fun IdentlisteProjection.identerDetailsFragment() {
    identer {
        ident()
        gruppe()
        historisk()
    }
}
