package no.nav.dagpenger.grunnbelop

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

enum class Grunnbeløp(val verdi: BigDecimal, val iverksattFom: LocalDate) {
    GjusteringsTest(verdi = 100000.toBigDecimal(), iverksattFom = LocalDate.now().plusYears(10)),
    FastsattI2019(verdi = 99858.toBigDecimal(), iverksattFom = LocalDate.of(2019, Month.MAY, 26)),
    FastsattI2018(verdi = 96883.toBigDecimal(), iverksattFom = LocalDate.of(2018, Month.JUNE, 3)),
    FastsattI2017(verdi = 93634.toBigDecimal(), iverksattFom = LocalDate.of(2017, Month.MAY, 28)),
    FastsattI2016(verdi = 92576.toBigDecimal(), iverksattFom = LocalDate.of(2016, Month.MAY, 29)),
    FastsattI2015(verdi = 90068.toBigDecimal(), iverksattFom = LocalDate.of(2015, Month.MAY, 31))
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
            fom = LocalDate.of(2019, Month.MAY, 27)
        )
    ),
    Grunnbeløp.FastsattI2018 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2018, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2018, Month.JUNE, 4)
        )
    ),
    Grunnbeløp.FastsattI2017 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2017, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2017, Month.MAY, 29)
        )
    ),
    Grunnbeløp.FastsattI2016 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2016, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2016, Month.MAY, 30)
        )
    ),
    Grunnbeløp.FastsattI2015 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2015, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2015, Month.JUNE, 1)
        )
    )
)

private val grunnbeløp = gyldighetsperioder.flatMap { (grunnbeløp, mappings) ->
    mappings.map { (regel, mapping) ->
        GrunnbeløpPolicy(
            fom = mapping.fom,
            grunnbeløp = grunnbeløp,
            regel = regel,
            iverksattFom = grunnbeløp.iverksattFom
        )
    }
}.toSet().sortedByDescending { it.fom }

@Deprecated(
    message = "Du må angi regel først, så måned",
    replaceWith = ReplaceWith("getGrunnbeløpForRegel(regel).forMåned(dato)")
)
fun getGrunnbeløpForMåned(dato: YearMonth): GrunnbeløpPolicy {
    return getGrunnbeløpForRegel(Regel.Grunnlag)
        .first { it.gjelderFor(LocalDate.of(dato.year, dato.month, 10)) }
}

@Deprecated(
    message = "Du må angi regel først, så dato",
    replaceWith = ReplaceWith("getGrunnbeløpForRegel(regel).forDato(dato)")
)
fun getGrunnbeløpForDato(dato: LocalDate): GrunnbeløpPolicy {
    return getGrunnbeløpForRegel(Regel.Grunnlag)
        .first { it.gjelderFor(dato) }
}

fun getGrunnbeløpForRegel(regel: Regel): Set<GrunnbeløpPolicy> {
    return grunnbeløp.filter { it.gjelderFor(regel) }.toSet()
}

fun Set<GrunnbeløpPolicy>.forDato(dato: LocalDate): Grunnbeløp {
    return utenFramtidigeGrunnbeløp()
        .first { it.gjelderFor(dato) }
        .grunnbeløp
}

fun Set<GrunnbeløpPolicy>.forMåned(dato: YearMonth): Grunnbeløp {
    return utenFramtidigeGrunnbeløp()
        .first { it.gjelderFor(LocalDate.of(dato.year, dato.month, 10)) }
        .grunnbeløp
}

private fun Set<GrunnbeløpPolicy>.utenFramtidigeGrunnbeløp(): List<GrunnbeløpPolicy> {
    val dato = LocalDate.now()
    return this.filter { it.iverksattFom.isBefore(dato).or(it.iverksattFom.isEqual(dato)) }
}

private fun GrunnbeløpPolicy.gjelderFor(dato: LocalDate): Boolean {
    return !(dato.isBefore(this.fom))
}

private fun GrunnbeløpPolicy.gjelderFor(regel: Regel): Boolean {
    return this.regel == regel
}

data class GrunnbeløpPolicy(
    val fom: LocalDate,
    val grunnbeløp: Grunnbeløp,
    val regel: Regel,
    val iverksattFom: LocalDate
)

internal data class Gyldighetsperiode(
    val fom: LocalDate
)
