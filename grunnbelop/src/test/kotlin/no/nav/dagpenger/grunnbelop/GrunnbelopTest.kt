package no.nav.dagpenger.grunnbelop

import io.kotlintest.inspectors.forAll
import io.kotlintest.inspectors.forOne
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

class GrunnbelopTest {
    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned mai 2020 `() {
        Assertions.assertEquals(
            99858.toBigDecimal(),
            getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(YearMonth.of(2020, Month.MAY)).grunnbeløp.verdi
        )
    }

    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned april 2020 `() {
        Assertions.assertEquals(
            99858.toBigDecimal(),
            getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(YearMonth.of(2020, Month.APRIL)).grunnbeløp.verdi
        )
    }

    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned may 2019 `() {
        Assertions.assertEquals(
            99858.toBigDecimal(),
            getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(YearMonth.of(2019, Month.MAY)).grunnbeløp.verdi
        )
    }

    @Test
    fun ` Skal returnere grunnbeløp på 96883 for måned april 2019 `() {
        Assertions.assertEquals(
            96883.toBigDecimal(),
            getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(YearMonth.of(2019, Month.APRIL)).grunnbeløp.verdi
        )
    }

    @Test
    fun ` Skal returnere grunnbeløp på 93634 for måned mars 2018 `() {
        Assertions.assertEquals(
            93634.toBigDecimal(),
            getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(YearMonth.of(2018, Month.MARCH)).grunnbeløp.verdi
        )
    }

    @Test
    fun ` Skal returnere grunnbeløp på 92576 for måned mai 2016  `() {
        Assertions.assertEquals(
            92576.toBigDecimal(),
            getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(YearMonth.of(2016, Month.MAY)).grunnbeløp.verdi
        )
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for måned august 2015 `() {
        Assertions.assertEquals(
            90068.toBigDecimal(),
            getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(YearMonth.of(2015, Month.AUGUST)).grunnbeløp.verdi
        )
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for dato 06082015 `() {
        Assertions.assertEquals(
            90068.toBigDecimal(),
            getGrunnbeløpForRegel(Regel.Grunnlag).forDato(LocalDate.of(2015, 8, 6)).grunnbeløp.verdi
        )
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for dato 01052019 `() {
        Assertions.assertEquals(
            99858.toBigDecimal(),
            getGrunnbeløpForRegel(Regel.Grunnlag).forDato(LocalDate.of(2019, 5, 1)).grunnbeløp.verdi
        )
    }

    @Test
    fun `Skal finne faktoren mellom to Grunnbeløp med desimaler`() {

        val grunnbeløp = GrunnbeløpMapping(LocalDate.now(), LocalDate.now(), Grunnbeløp.FastsattI2017)
        val gjeldendeGrunnbeløp = GrunnbeløpMapping(LocalDate.now(), LocalDate.now(), Grunnbeløp.FastsattI2018)

        Assertions.assertEquals(BigDecimal("1.03469893414785227588"), gjeldendeGrunnbeløp.faktorMellom(grunnbeløp))
    }

    @Test
    fun `Alle kombinasjoner av år og regler har en mapping`() {
        Grunnbeløp.values().forAll { beløp ->
            Regel.values().forAll { regel ->
                val grunnbeløpForRegel = getGrunnbeløpForRegel(regel)
                grunnbeløpForRegel.shouldBeInstanceOf<Set<GrunnbeløpMapping>>()

                grunnbeløpForRegel.forOne {
                    it.grunnbeløp shouldBe beløp
                }
            }
        }
    }
}
