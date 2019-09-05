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
            fom = LocalDate.of(2019, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2019, Month.MAY, 1)
        )
    ),
    Grunnbeløp.FastsattI2018 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2018, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2018, Month.MAY, 1)
        )
    ),
    Grunnbeløp.FastsattI2017 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2017, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2017, Month.MAY, 1)
        )
    ),
    Grunnbeløp.FastsattI2016 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2016, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2016, Month.MAY, 1)
        )
    ),
    Grunnbeløp.FastsattI2015 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2015, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2015, Month.MAY, 1)
        )
    ),
    Grunnbeløp.FastsattI2015 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2015, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2015, Month.MAY, 1)
        )
    )
)

private val grunnbeløp = gyldighetsperioder.flatMap { (grunnbeløp, mappings) ->
    mappings.map { (regel, mapping) ->
        GrunnbeløpMapping(
            fom = mapping.fom,
            grunnbeløp = grunnbeløp,
            regel = regel,
            iverksattFom = mapping.iverksattFom ?: mapping.fom
        )
    }
}.toSet().sortedByDescending { it.fom }

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
    return utenFramtidigeGrunnbeløp()
        .first { it.gjelderFor(dato) }
}

fun Set<GrunnbeløpMapping>.forMåned(dato: YearMonth): GrunnbeløpMapping {
    return this.first { it.gjelderFor(LocalDate.of(dato.year, dato.month, 10)) }
}

private fun Set<GrunnbeløpMapping>.utenFramtidigeGrunnbeløp(): List<GrunnbeløpMapping> {
    val dato = LocalDate.now()
    return this.filter { it.iverksattFom.isBefore(dato).or(it.iverksattFom.isEqual(dato)) }
}

fun GrunnbeløpMapping.gjelderFor(dato: LocalDate): Boolean {
    return !(dato.isBefore(this.fom))
}

fun GrunnbeløpMapping.gjelderFor(regel: Regel): Boolean {
    return this.regel == regel
}

data class GrunnbeløpMapping(
    val fom: LocalDate,
    val grunnbeløp: Grunnbeløp,
    val regel: Regel,
    val iverksattFom: LocalDate
)

internal data class Gyldighetsperiode(
    val fom: LocalDate,
    val iverksattFom: LocalDate? = null
)
