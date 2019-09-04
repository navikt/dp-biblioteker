package no.nav.dagpenger.grunnbelop

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

data class GrunnbeløpMapping(
    val fom: LocalDate,
    val tom: LocalDate,
    val grunnbeløp: Grunnbeløp,
    val regel: Regel = Regel.Grunnlag
) {
    fun faktorMellom(grunnbeløp: GrunnbeløpMapping): BigDecimal {
        return this.grunnbeløp.verdi.divide(grunnbeløp.grunnbeløp.verdi, antallDesimaler, RoundingMode.HALF_UP)
    }
}

const val antallDesimaler: Int = 20

enum class Regel {
    Minsteinntekt,
    Grunnlag
}

enum class Grunnbeløp(val verdi: BigDecimal) {
    FastsattI2019(verdi = 99858.toBigDecimal()),
    FastsattI2018(verdi = 96883.toBigDecimal()),
    FastsattI2017(verdi = 93634.toBigDecimal()),
    FastsattI2016(verdi = 92576.toBigDecimal()),
    FastsattI2015(verdi = 90068.toBigDecimal())
}

private val grunnbeløp: Set<GrunnbeløpMapping> = mapOf(
    Regel.Grunnlag to setOf(
        GrunnbeløpMapping(
            fom = LocalDate.of(2019, Month.MAY, 1),
            tom = LocalDate.of(2020, Month.APRIL, 30),
            grunnbeløp = Grunnbeløp.FastsattI2019,
            regel = Regel.Grunnlag
        ),
        GrunnbeløpMapping(
            fom = LocalDate.of(2018, Month.MAY, 1),
            tom = LocalDate.of(2019, Month.APRIL, 30),
            grunnbeløp = Grunnbeløp.FastsattI2018,
            regel = Regel.Grunnlag
        ),
        GrunnbeløpMapping(
            fom = LocalDate.of(2017, Month.MAY, 1),
            tom = LocalDate.of(2018, Month.APRIL, 30),
            grunnbeløp = Grunnbeløp.FastsattI2017,
            regel = Regel.Grunnlag
        ),
        GrunnbeløpMapping(
            fom = LocalDate.of(2016, Month.MAY, 1),
            tom = LocalDate.of(2017, Month.APRIL, 30),
            grunnbeløp = Grunnbeløp.FastsattI2016,
            regel = Regel.Grunnlag
        ),
        GrunnbeløpMapping(
            fom = LocalDate.of(2015, Month.MAY, 1),
            tom = LocalDate.of(2016, Month.APRIL, 30),
            grunnbeløp = Grunnbeløp.FastsattI2015,
            regel = Regel.Grunnlag
        )
    ),
    Regel.Minsteinntekt to setOf(
        GrunnbeløpMapping(
            fom = LocalDate.of(2019, Month.MAY, 1),
            tom = LocalDate.of(2020, Month.APRIL, 30),
            grunnbeløp = Grunnbeløp.FastsattI2019,
            regel = Regel.Minsteinntekt
        ),
        GrunnbeløpMapping(
            fom = LocalDate.of(2018, Month.MAY, 1),
            tom = LocalDate.of(2019, Month.APRIL, 30),
            grunnbeløp = Grunnbeløp.FastsattI2018,
            regel = Regel.Minsteinntekt
        ),
        GrunnbeløpMapping(
            fom = LocalDate.of(2017, Month.MAY, 1),
            tom = LocalDate.of(2018, Month.APRIL, 30),
            grunnbeløp = Grunnbeløp.FastsattI2017,
            regel = Regel.Minsteinntekt
        ),
        GrunnbeløpMapping(
            fom = LocalDate.of(2016, Month.MAY, 1),
            tom = LocalDate.of(2017, Month.APRIL, 30),
            grunnbeløp = Grunnbeløp.FastsattI2016,
            regel = Regel.Minsteinntekt
        ),
        GrunnbeløpMapping(
            fom = LocalDate.of(2015, Month.MAY, 1),
            tom = LocalDate.of(2016, Month.APRIL, 30),
            grunnbeløp = Grunnbeløp.FastsattI2015,
            regel = Regel.Minsteinntekt
        )
    )
).values.flatten().toSet()

@Deprecated(
    message = "Du må angi regel først, så måned",
    replaceWith = ReplaceWith("getGrunnbeløpForRegel(regel).forMåned(dato)")
)
fun getGrunnbeløpForMåned(dato: YearMonth): GrunnbeløpMapping {
    return grunnbeløp.first { it.gjelderFor(LocalDate.of(dato.year, dato.month, 10)) }
}

@Deprecated(
    message = "Du må angi regel først, så dato",
    replaceWith = ReplaceWith("getGrunnbeløpForRegel(regel).forDato(dato)")
)
fun getGrunnbeløpForDato(dato: LocalDate): GrunnbeløpMapping {
    return grunnbeløp.first { it.gjelderFor(dato) }
}

fun getGrunnbeløpForRegel(regel: Regel): Set<GrunnbeløpMapping> {
    return grunnbeløp.filter { it.gjelderFor(regel) }.toSet()
}

fun Set<GrunnbeløpMapping>.forDato(dato: LocalDate): GrunnbeløpMapping {
    return this.first { it.gjelderFor(LocalDate.of(dato.year, dato.month, 10)) }
}

fun Set<GrunnbeløpMapping>.forMåned(dato: YearMonth): GrunnbeløpMapping {
    return this.first { it.gjelderFor(LocalDate.of(dato.year, dato.month, 10)) }
}

fun GrunnbeløpMapping.gjelderFor(dato: LocalDate): Boolean {
    return !(dato.isBefore(this.fom))
}

fun GrunnbeløpMapping.gjelderFor(regel: Regel): Boolean {
    return this.regel == regel
}
