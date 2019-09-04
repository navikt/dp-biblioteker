package no.nav.dagpenger.grunnbelop

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

enum class Grunnbeløp(val verdi: BigDecimal) {
    FastsattI2019(verdi = 99858.toBigDecimal()),
    FastsattI2018(verdi = 96883.toBigDecimal()),
    FastsattI2017(verdi = 93634.toBigDecimal()),
    FastsattI2016(verdi = 92576.toBigDecimal()),
    FastsattI2015(verdi = 90068.toBigDecimal())
}

fun Grunnbeløp.faktorMellom(grunnbeløp: Grunnbeløp): BigDecimal {
    return this.verdi.divide(grunnbeløp.verdi, antallDesimaler, RoundingMode.HALF_UP)
}

internal const val antallDesimaler: Int = 20

enum class Regel {
    Minsteinntekt,
    Grunnlag
}

internal val gyldighetsperioder = mapOf(
    Grunnbeløp.FastsattI2019 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2019, Month.MAY, 1),
            tom = LocalDate.of(2020, Month.APRIL, 30)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2019, Month.MAY, 1),
            tom = LocalDate.of(2020, Month.MAY, 19)
        )
    ),
    Grunnbeløp.FastsattI2018 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2018, Month.MAY, 1),
            tom = LocalDate.of(2019, Month.APRIL, 30)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2018, Month.MAY, 1),
            tom = LocalDate.of(2019, Month.APRIL, 30)
        )
    ),
    Grunnbeløp.FastsattI2017 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2017, Month.MAY, 1),
            tom = LocalDate.of(2018, Month.APRIL, 30)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2017, Month.MAY, 1),
            tom = LocalDate.of(2018, Month.APRIL, 30)
        )
    ),
    Grunnbeløp.FastsattI2016 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2016, Month.MAY, 1),
            tom = LocalDate.of(2017, Month.APRIL, 30)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2016, Month.MAY, 1),
            tom = LocalDate.of(2017, Month.APRIL, 30)
        )
    ),
    Grunnbeløp.FastsattI2015 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2015, Month.MAY, 1),
            tom = LocalDate.of(2016, Month.APRIL, 30)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2015, Month.MAY, 1),
            tom = LocalDate.of(2016, Month.APRIL, 30)
        )
    ),
    Grunnbeløp.FastsattI2015 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2015, Month.MAY, 1),
            tom = LocalDate.of(2016, Month.APRIL, 30)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2015, Month.MAY, 1),
            tom = LocalDate.of(2016, Month.APRIL, 30)
        )
    )
)

private val grunnbeløp = gyldighetsperioder.flatMap { (grunnbeløp, mappings) ->
    mappings.map { (regel, mapping) ->
        GrunnbeløpMapping(
            regel = regel,
            fom = mapping.fom,
            tom = mapping.tom,
            grunnbeløp = grunnbeløp
        )
    }
}.toSet()

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

data class GrunnbeløpMapping(
    val fom: LocalDate,
    val tom: LocalDate,
    val grunnbeløp: Grunnbeløp,
    val regel: Regel
)

internal data class Gyldighetsperiode(
    val fom: LocalDate,
    val tom: LocalDate
)
