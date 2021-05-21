package no.nav.dagpenger.grunnbelop

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

enum class Grunnbeløp(val verdi: BigDecimal, val iverksattFom: LocalDate) {
    GjusteringsTest(verdi = 106399.toBigDecimal(), iverksattFom = LocalDate.now().plusYears(10)),
    FastsattI2021(verdi = 106399.toBigDecimal(), iverksattFom = LocalDate.of(2021, Month.MAY, 23)),
    FastsattI2020(verdi = 101351.toBigDecimal(), iverksattFom = LocalDate.of(2020, Month.SEPTEMBER, 19)),
    FastsattI2019(verdi = 99858.toBigDecimal(), iverksattFom = LocalDate.of(2019, Month.MAY, 26)),
    FastsattI2018(verdi = 96883.toBigDecimal(), iverksattFom = LocalDate.of(2018, Month.JUNE, 3)),
    FastsattI2017(verdi = 93634.toBigDecimal(), iverksattFom = LocalDate.of(2017, Month.MAY, 28)),
    FastsattI2016(verdi = 92576.toBigDecimal(), iverksattFom = LocalDate.of(2016, Month.MAY, 29)),
    FastsattI2015(verdi = 90068.toBigDecimal(), iverksattFom = LocalDate.of(2015, Month.MAY, 31)),
    FastsattI2014(verdi = 88370.toBigDecimal(), iverksattFom = LocalDate.of(2014, Month.JUNE, 29)),
    FastsattI2013(verdi = 85245.toBigDecimal(), iverksattFom = LocalDate.of(2013, Month.JUNE, 23)),
    FastsattI2012(verdi = 82122.toBigDecimal(), iverksattFom = LocalDate.of(2012, Month.JULY, 1)),
    FastsattI2011(verdi = 79216.toBigDecimal(), iverksattFom = LocalDate.of(2011, Month.JUNE, 26)),
    FastsattI2010(verdi = 75641.toBigDecimal(), iverksattFom = LocalDate.of(2010, Month.JUNE, 27)),
    FastsattI2009(verdi = 72881.toBigDecimal(), iverksattFom = LocalDate.of(2009, Month.JUNE, 28)),
    FastsattI2008(verdi = 70256.toBigDecimal(), iverksattFom = LocalDate.of(2008, Month.JUNE, 29)),
    FastsattI2007(verdi = 66812.toBigDecimal(), iverksattFom = LocalDate.of(2007, Month.JUNE, 24)),
    FastsattI2006(verdi = 62892.toBigDecimal(), iverksattFom = LocalDate.of(2006, Month.JUNE, 25)),
    FastsattI2005(verdi = 60699.toBigDecimal(), iverksattFom = LocalDate.of(2005, Month.JUNE, 26)),
    FastsattI2004(verdi = 58778.toBigDecimal(), iverksattFom = LocalDate.of(2004, Month.JUNE, 27)),
    FastsattI2003(verdi = 56861.toBigDecimal(), iverksattFom = LocalDate.of(2003, Month.JUNE, 29)),
    FastsattI2002(verdi = 54170.toBigDecimal(), iverksattFom = LocalDate.of(2002, Month.JUNE, 30)),
    FastsattI2001(verdi = 51360.toBigDecimal(), iverksattFom = LocalDate.of(2001, Month.JUNE, 24)),
    FastsattI2000(verdi = 49090.toBigDecimal(), iverksattFom = LocalDate.of(2000, Month.APRIL, 30)),
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

    Grunnbeløp.FastsattI2021 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2021, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2021, Month.MAY, 24)
        )
    ),

    Grunnbeløp.FastsattI2020 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2020, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2020, Month.SEPTEMBER, 21)
        )
    ),

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
    ),
    Grunnbeløp.FastsattI2014 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2014, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2014, Month.JUNE, 30)
        )
    ),
    Grunnbeløp.FastsattI2013 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2013, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2013, Month.JUNE, 24)
        )
    ),
    Grunnbeløp.FastsattI2012 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2012, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2012, Month.JULY, 2)
        )
    ),
    Grunnbeløp.FastsattI2011 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2011, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2011, Month.JUNE, 27)
        )
    ),
    Grunnbeløp.FastsattI2010 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2010, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2010, Month.JUNE, 28)
        )
    ),
    Grunnbeløp.FastsattI2009 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2009, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2009, Month.JUNE, 29)
        )
    ),
    Grunnbeløp.FastsattI2008 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2008, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2008, Month.JUNE, 30)
        )
    ),
    Grunnbeløp.FastsattI2007 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2007, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2007, Month.JUNE, 25)
        )
    ),
    Grunnbeløp.FastsattI2006 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2006, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2006, Month.JUNE, 26)
        )
    ),
    Grunnbeløp.FastsattI2005 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2005, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2005, Month.JUNE, 27)
        )
    ),
    Grunnbeløp.FastsattI2004 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2004, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2004, Month.JUNE, 28)
        )
    ),
    Grunnbeløp.FastsattI2003 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2003, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2003, Month.JUNE, 30)
        )
    ),
    Grunnbeløp.FastsattI2002 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2002, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2002, Month.JULY, 1)
        )
    ),
    Grunnbeløp.FastsattI2001 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2001, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2001, Month.JUNE, 25)
        )
    ),
    Grunnbeløp.FastsattI2000 to mapOf(
        Regel.Grunnlag to Gyldighetsperiode(
            fom = LocalDate.of(2000, Month.MAY, 1)
        ),
        Regel.Minsteinntekt to Gyldighetsperiode(
            fom = LocalDate.of(2000, Month.MAY, 1)
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

fun getGrunnbeløpForRegel(regel: Regel): Set<GrunnbeløpPolicy> {
    return grunnbeløp.filter { it.gjelderFor(regel) }.toSet()
}

fun Set<GrunnbeløpPolicy>.forDato(dato: LocalDate, gjeldendeDato: LocalDate = LocalDate.now()): Grunnbeløp {
    return utenFramtidigeGrunnbeløp(gjeldendeDato)
        .first { it.gjelderFor(dato) }
        .grunnbeløp
}

fun Set<GrunnbeløpPolicy>.forMåned(dato: YearMonth, gjeldendeDato: LocalDate = LocalDate.now()): Grunnbeløp {
    return utenFramtidigeGrunnbeløp(gjeldendeDato)
        .first { it.gjelderFor(LocalDate.of(dato.year, dato.month, 10)) }
        .grunnbeløp
}

private fun Set<GrunnbeløpPolicy>.utenFramtidigeGrunnbeløp(gjeldendeDato: LocalDate): List<GrunnbeløpPolicy> {
    return this.filter { it.iverksattFom.isBefore(gjeldendeDato).or(it.iverksattFom.isEqual(gjeldendeDato)) }
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
