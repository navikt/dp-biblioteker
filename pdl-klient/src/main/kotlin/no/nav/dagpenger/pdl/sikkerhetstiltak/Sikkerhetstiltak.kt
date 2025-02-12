package no.nav.dagpenger.pdl.sikkerhetstiltak

import java.time.LocalDate

data class SikkerhetstiltakDto(
    val tiltakstype: String,
    val tiltaksbeskrivelse: String,
    val kontaktperson: Kontaktperson?,
    val gyldigFraOgMed: LocalDate,
    val gyldigTilOgMed: LocalDate,
)

data class Kontaktperson(
    val personident: String,
    val enhet: String,
)
