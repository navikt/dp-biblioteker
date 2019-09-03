package no.nav.dagpenger.grunnbelop

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

class GrunnbelopTest {

    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned mai 2020 `() {

        Assertions.assertEquals(99858.toBigDecimal(), getGrunnbeløpForMåned(YearMonth.of(2020, Month.MAY)).verdi)
    }

    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned april 2020 `() {

        Assertions.assertEquals(99858.toBigDecimal(), getGrunnbeløpForMåned(YearMonth.of(2020, Month.APRIL)).verdi)
    }

    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned may 2019 `() {

        Assertions.assertEquals(99858.toBigDecimal(), getGrunnbeløpForMåned(YearMonth.of(2019, Month.MAY)).verdi)
    }

    @Test
    fun ` Skal returnere grunnbeløp på 96883 for måned april 2019 `() {

        Assertions.assertEquals(96883.toBigDecimal(), getGrunnbeløpForMåned(YearMonth.of(2019, Month.APRIL)).verdi)
    }

    @Test
    fun ` Skal returnere grunnbeløp på 93634 for måned mars 2018 `() {

        Assertions.assertEquals(93634.toBigDecimal(), getGrunnbeløpForMåned(YearMonth.of(2018, Month.MARCH)).verdi)
    }

    @Test
    fun ` Skal returnere grunnbeløp på 92576 for måned mai 2016  `() {

        Assertions.assertEquals(92576.toBigDecimal(), getGrunnbeløpForMåned(YearMonth.of(2016, Month.MAY)).verdi)
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for måned august 2015 `() {

        Assertions.assertEquals(90068.toBigDecimal(), getGrunnbeløpForMåned(YearMonth.of(2015, Month.AUGUST)).verdi)
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for dato 06082015 `() {

        Assertions.assertEquals(90068.toBigDecimal(), getGrunnbeløpForDato(LocalDate.of(2015, 8, 6)).verdi)
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for dato 01052019 `() {
        Assertions.assertEquals(99858.toBigDecimal(), getGrunnbeløpForDato(LocalDate.of(2019, 5, 1)).verdi)
    }

    @Test
    fun ` Skal returnere finne grunnbeløp med strategier`() {
        val grunnbeløpGrunnlag = getGrunnbeløp()
            .forDato(LocalDate.of(2019,5,1))
            .forRegel(Regel.Grunnlag)
            .first()

        Assertions.assertEquals(99858.toBigDecimal(), grunnbeløpGrunnlag.verdi)

        val grunnbeløpMinsteinntekt = getGrunnbeløp()
            .forDato(LocalDate.of(2019,5,1))
            .forRegel(Regel.Minsteinntekt)
            .first()

        Assertions.assertEquals(8.toBigDecimal(), grunnbeløpMinsteinntekt.verdi)

        val grunnbeløpForHva = getGrunnbeløp()
            .forDato(LocalDate.of(2019,5,1))
            .first()

        Assertions.assertEquals(99858.toBigDecimal(), grunnbeløpForHva.verdi)
    }

    @Test
    fun `Skal finne faktoren mellom to Grunnbeløp med desimaler`() {

        val grunnbeløp = Grunnbeløp(LocalDate.now(), LocalDate.now(), 93634.toBigDecimal())
        val gjeldendeGrunnbeløp = Grunnbeløp(LocalDate.now(), LocalDate.now(), 96883.toBigDecimal())

        Assertions.assertEquals(BigDecimal("1.03469893414785227588"), gjeldendeGrunnbeløp.faktorMellom(grunnbeløp))
    }
}