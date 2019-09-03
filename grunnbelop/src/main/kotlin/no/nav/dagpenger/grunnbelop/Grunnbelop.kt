package no.nav.dagpenger.grunnbelop

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

data class Grunnbeløp(
    val fom: LocalDate,
    val tom: LocalDate,
    val verdi: BigDecimal,
    val regel: Regel = Regel.Grunnlag
) {
    fun faktorMellom(grunnbeløp: Grunnbeløp): BigDecimal {

        return this.verdi.divide(grunnbeløp.verdi, antallDesimaler, RoundingMode.HALF_UP)
    }
}

const val antallDesimaler: Int = 20

private val grunnbeløp: Set<Grunnbeløp> = setOf(
    Grunnbeløp(
        fom = LocalDate.of(2019, Month.MAY, 1),
        tom = LocalDate.of(2020, Month.APRIL, 30),
        verdi = 99858.toBigDecimal(),
        regel = Regel.Grunnlag
    ),
    Grunnbeløp(
        fom = LocalDate.of(2019, Month.MAY, 1),
        tom = LocalDate.of(2020, Month.APRIL, 30),
        verdi = 8.toBigDecimal(),
        regel = Regel.Minsteinntekt
    ),
    Grunnbeløp(
        fom = LocalDate.of(2018, Month.MAY, 1),
        tom = LocalDate.of(2019, Month.APRIL, 30),
        verdi = 96883.toBigDecimal(),
        regel = Regel.Grunnlag
    ),
    Grunnbeløp(
        fom = LocalDate.of(2017, Month.MAY, 1),
        tom = LocalDate.of(2018, Month.APRIL, 30),
        verdi = 93634.toBigDecimal(),
        regel = Regel.Grunnlag
    ),
    Grunnbeløp(
        fom = LocalDate.of(2016, Month.MAY, 1),
        tom = LocalDate.of(2017, Month.APRIL, 30),
        verdi = 92576.toBigDecimal(),
        regel = Regel.Grunnlag
    ),
    Grunnbeløp(
        fom = LocalDate.of(2015, Month.MAY, 1),
        tom = LocalDate.of(2016, Month.APRIL, 30),
        verdi = 90068.toBigDecimal(),
        regel = Regel.Grunnlag
    )
)

fun getGrunnbeløpForMåned(dato: YearMonth): Grunnbeløp {
    return grunnbeløp.first { it.gjelderFor(LocalDate.of(dato.year, dato.month, 10)) }
}

fun getGrunnbeløpForDato(dato: LocalDate): Grunnbeløp {
    return grunnbeløp.first { it.gjelderFor(dato) }
}

fun getGrunnbeløp(): Set<Grunnbeløp> {
    return grunnbeløp
}

fun Set<Grunnbeløp>.forDato(dato: LocalDate): Set<Grunnbeløp> {
    return this.filter { it.gjelderFor(LocalDate.of(dato.year, dato.month, 10)) }.toSet()
}

fun Set<Grunnbeløp>.forRegel(regel: Regel): Set<Grunnbeløp> {
    return this.filter { it.gjelderFor(regel) }.toSet()
}

enum class Regel {
    Minsteinntekt,
    Grunnlag
}

fun Grunnbeløp.gjelderFor(dato: LocalDate): Boolean {
    return !(dato.isBefore(this.fom))
}

fun Grunnbeløp.gjelderFor(regel: Regel): Boolean {
    return this.regel == regel
}
