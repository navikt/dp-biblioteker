package no.nav.dagpenger.pdl

import no.nav.dagpenger.pdl.PDLPerson.Kjonn
import no.nav.dagpenger.pdl.dto.PDLAdresse
import java.time.LocalDate

interface PersonaliaVisitor {
    fun visit(
        fodselnummer: String,
        fodselsdato: LocalDate,
        alder: Long,
        adressebeskyttelseGradering: PDLPerson.AdressebeskyttelseGradering,
        fornavn: String,
        mellomNavn: String?,
        etternavn: String,
        statsborgerskap: String,
        kjonn: Kjonn
    ) {
    }
}

interface BostedsAdresseVisitor : MatrikkelAdresseVisitor, VegAdresseVisitor, UtenlandskAdresseVisitor {
    fun visitUkjentBosted(
        adresseMetadata: AdresseMetadata,
        bostedskommune: String?
    ) {
    }
}

interface OppholdsAdresseVisitor : MatrikkelAdresseVisitor, VegAdresseVisitor, UtenlandskAdresseVisitor

interface KontaktAdresseVisitor : VegAdresseVisitor, UtenlandskAdresseVisitor {
    fun visitPostAdresseIFrittFormat(adresse: PDLAdresse.PostAdresseIFrittFormat) {}

    fun visitPostboksadresse(adresse: PDLAdresse.PostboksAdresse) {}

    fun visitUtenlandskAdresseIFrittFormat(adresse: PDLAdresse.UtenlandsAdresseIFrittFormat) {}
}

interface UtenlandskAdresseVisitor {
    fun visitUtenlandskAdresse(adresse: PDLAdresse.UtenlandskAdresse) {}
}

interface MatrikkelAdresseVisitor {
    fun visitMatrikkelAdresse(adresse: PDLAdresse.MatrikkelAdresse) {}
}

interface VegAdresseVisitor {
    fun visitVegAdresse(adresse: PDLAdresse.VegAdresse) {}
}
