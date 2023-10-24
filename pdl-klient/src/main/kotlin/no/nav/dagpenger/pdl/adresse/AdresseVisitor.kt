package no.nav.dagpenger.pdl.adresse

import no.nav.dagpenger.pdl.BostedsAdresseVisitor
import no.nav.dagpenger.pdl.KontaktAdresseVisitor
import no.nav.dagpenger.pdl.OppholdsAdresseVisitor
import no.nav.dagpenger.pdl.PDLPerson
import no.nav.dagpenger.pdl.PersonaliaVisitor
import java.time.LocalDate

class AdresseVisitor(pdlPerson: PDLPerson) :
    BostedsAdresseVisitor,
    KontaktAdresseVisitor,
    OppholdsAdresseVisitor,
    PersonaliaVisitor {
    private val mutableAdresseList: MutableList<PDLAdresse> = mutableListOf()
    private lateinit var adressebeskyttelseGradering: PDLPerson.AdressebeskyttelseGradering

    init {
        pdlPerson.acceptPersonaliaVisitor(this)
        pdlPerson.acceptBostedsAdresseVisitor(this)
        pdlPerson.acceptOppholdsAdressseVisitor(this)
        pdlPerson.acceptKontaktAdresseVisitor(this)
        mutableAdresseList
            .filter { it.adresseMetadata.erGyldig }
            .filter { this.harIkkeHemmeligAdresse() }
    }

    val adresser: List<PDLAdresse> = mutableAdresseList
        .filter { it.adresseMetadata.erGyldig }
        .filter { this.harIkkeHemmeligAdresse() }
        .toList()

    val bostedsadresse: PDLAdresse? =
        adresser.singleOrNull { it.adresseMetadata.adresseType == AdresseMetadata.AdresseType.BOSTEDSADRESSE }

    private fun harIkkeHemmeligAdresse(): Boolean {
        return PDLPerson.AdressebeskyttelseGradering.UGRADERT == adressebeskyttelseGradering
    }

    override fun visit(
        fodselnummer: String,
        fodselsdato: LocalDate,
        alder: Long,
        adressebeskyttelseGradering: PDLPerson.AdressebeskyttelseGradering,
        fornavn: String,
        mellomNavn: String?,
        etternavn: String,
        statsborgerskap: String?,
        kjonn: PDLPerson.Kjonn,
    ) {
        this.adressebeskyttelseGradering = adressebeskyttelseGradering
    }

    override fun visitMatrikkelAdresse(
        adresse: PDLAdresse.MatrikkelAdresse,
    ) {
        mutableAdresseList.add(adresse)
    }

    override fun visitVegAdresse(adresse: PDLAdresse.VegAdresse) {
        mutableAdresseList.add(adresse)
    }

    override fun visitPostAdresseIFrittFormat(
        adresse: PDLAdresse.PostAdresseIFrittFormat,
    ) {
        mutableAdresseList.add(adresse)
    }

    override fun visitPostboksadresse(
        adresse: PDLAdresse.PostboksAdresse,
    ) {
        mutableAdresseList.add(adresse)
    }

    override fun visitUtenlandskAdresseIFrittFormat(
        adresse: PDLAdresse.UtenlandsAdresseIFrittFormat,
    ) {
        mutableAdresseList.add(adresse)
    }

    override fun visitUtenlandskAdresse(
        adresse: PDLAdresse.UtenlandskAdresse,
    ) {
        mutableAdresseList.add(adresse)
    }
}
