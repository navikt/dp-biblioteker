package no.nav.dagpenger.grunnbelop

import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

data class Grunnbeløp(
    val fom: LocalDate,
    val tom: LocalDate,
    val verdi: BigDecimal
)

private val grunnbeløp: Set<Grunnbeløp> = setOf(
    Grunnbeløp(
        LocalDate.of(2019, Month.MAY, 1),
        LocalDate.of(2020, Month.APRIL, 30),
        99858.toBigDecimal()),
    Grunnbeløp(
        LocalDate.of(2018, Month.MAY, 1),
        LocalDate.of(2019, Month.APRIL, 30),
        96883.toBigDecimal()),
    Grunnbeløp(
        LocalDate.of(2017, Month.MAY, 1),
        LocalDate.of(2018, Month.APRIL, 30),
        93634.toBigDecimal()),
    Grunnbeløp(
        LocalDate.of(2016, Month.MAY, 1),
        LocalDate.of(2017, Month.APRIL, 30),
        92576.toBigDecimal()),
    Grunnbeløp(
        LocalDate.of(2015, Month.MAY, 1),
        LocalDate.of(2016, Month.APRIL, 30),
        90068.toBigDecimal())
)

fun getGrunnbeløpForMåned(dato: YearMonth): Grunnbeløp {
    return grunnbeløp.first { it.gjelderFor(LocalDate.of(dato.year, dato.month, 10)) }
}

fun getGrunnbeløpForDato(dato: LocalDate): Grunnbeløp {
    return grunnbeløp.first { it.gjelderFor(dato) }
}

fun Grunnbeløp.gjelderFor(dato: LocalDate): Boolean {
    return !(dato.isBefore(this.fom) || (dato.isAfter(this.tom)))
}