package no.nav.dagpenger.pdl.dto

import no.nav.dagpenger.pdl.AdresseMetadata

sealed class PDLAdresse(open val adresseMetadata: AdresseMetadata) {
    data class VegAdresse(
        override val adresseMetadata: AdresseMetadata,
        val adressenavn: String? = null,
        val bruksenhetsnummer: String? = null,
        val bydelsnummer: String? = null,
        val husbokstav: String? = null,
        val husnummer: String? = null,
        val kommunenummer: String? = null,
        val postnummer: String? = null,
        val tilleggsnavn: String? = null
    ) : PDLAdresse(adresseMetadata)

    data class MatrikkelAdresse(
        override val adresseMetadata: AdresseMetadata,
        val bruksenhetsnummer: String? = null,
        val kommunenummer: String? = null,
        val matrikkelId: String? = null,
        val postnummer: String? = null,
        val tilleggsnavn: String? = null
    ) : PDLAdresse(adresseMetadata)

    data class PostAdresseIFrittFormat(
        override val adresseMetadata: AdresseMetadata,
        val adresseLinje1: String? = null,
        val adresseLinje2: String? = null,
        val adresseLinje3: String? = null,
        val postnummer: String? = null,
    ) : PDLAdresse(adresseMetadata)

    data class PostboksAdresse(
        override val adresseMetadata: AdresseMetadata,
        val postbokseier: String? = null,
        val postboks: String? = null,
        val postnummer: String? = null,
    ) : PDLAdresse(adresseMetadata)

    data class UtenlandsAdresseIFrittFormat(
        override val adresseMetadata: AdresseMetadata,
        val adresseLinje1: String? = null,
        val adresseLinje2: String? = null,
        val adresseLinje3: String? = null,
        val postkode: String? = null,
        val byEllerStedsnavn: String? = null,
        val landKode: String? = null
    ) : PDLAdresse(adresseMetadata)

    data class UtenlandskAdresse(
        override val adresseMetadata: AdresseMetadata,
        val adressenavnNummer: String? = null,
        val bySted: String? = null,
        val bygningEtasjeLeilighet: String? = null,
        val landKode: String? = null,
        val postboksNummerNavn: String? = null,
        val postkode: String? = null,
        val regionDistriktOmraade: String? = null
    ) : PDLAdresse(adresseMetadata)
}
