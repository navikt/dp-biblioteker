package no.nav.dagpenger.grunnbelop

import io.kotlintest.assertSoftly
import io.kotlintest.inspectors.forAll
import io.kotlintest.inspectors.forOne
import io.kotlintest.matchers.maps.shouldContainKey
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

class GrunnbelopTest {
    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned mai 2020 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2020,
                Month.MAY
            )
        ).grunnbeløp.verdi shouldBe 99858.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned april 2020 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2020,
                Month.APRIL
            )
        ).grunnbeløp.verdi shouldBe 99858.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned may 2019 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2019,
                Month.MAY
            )
        ).grunnbeløp.verdi shouldBe 99858.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 96883 for måned april 2019 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2019,
                Month.APRIL
            )
        ).grunnbeløp.verdi shouldBe 96883.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 93634 for måned mars 2018 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2018,
                Month.MARCH
            )
        ).grunnbeløp.verdi shouldBe 93634.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 92576 for måned mai 2016  `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2016,
                Month.MAY
            )
        ).grunnbeløp.verdi shouldBe 92576.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for måned august 2015 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2015,
                Month.AUGUST
            )
        ).grunnbeløp.verdi shouldBe 90068.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for dato 06082015 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forDato(
            LocalDate.of(
                2015,
                8,
                6
            )
        ).grunnbeløp.verdi shouldBe 90068.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for dato 01052019 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forDato(
            LocalDate.of(
                2019,
                5,
                1
            )
        ).grunnbeløp.verdi shouldBe 99858.toBigDecimal()
    }

    @Test
    fun `Skal finne faktoren mellom to Grunnbeløp med desimaler`() {
        val grunnbeløp = Grunnbeløp.FastsattI2017
        val gjeldendeGrunnbeløp = Grunnbeløp.FastsattI2018

        val faktor = gjeldendeGrunnbeløp.faktorMellom(grunnbeløp)
        assertSoftly {
            faktor shouldBe BigDecimal("1.03469893414785227588")
            faktor.scale() shouldBe 20
        }
    }

    @Test
    fun `Er det ikke definert noe gyldigsperiode som varer lenge nok, skal den nyeste brukes`() {
        Grunnbeløp.values().forAll { grunnbeløp ->
            gyldighetsperioder shouldContainKey grunnbeløp
        }

        getGrunnbeløpForRegel(Regel.Grunnlag).forDato(
            LocalDate.of(
                2099,
                8,
                6
            )
        ).grunnbeløp.verdi shouldBe Grunnbeløp.values().first().verdi
    }

    @Test
    fun `Spør man for langt tilbake i tid skal man ikke få et grunnbeløp`() {
        Grunnbeløp.values().forAll { grunnbeløp ->
            gyldighetsperioder shouldContainKey grunnbeløp
        }

        shouldThrow<NoSuchElementException> {
            getGrunnbeløpForRegel(Regel.Grunnlag).forDato(
                LocalDate.of(
                    1099,
                    8,
                    6
                )
            )
        }
    }

    @Test
    fun `Alle grunnbeløp har en mapping`() {
        Grunnbeløp.values().forAll { grunnbeløp ->
            gyldighetsperioder shouldContainKey grunnbeløp
        }
    }

    @Test
    fun `Alle regler har en mapping for hvert grunnbeløp`() {
        Regel.values().forAll { regel ->
            gyldighetsperioder.values.forAll {
                it shouldContainKey regel
            }
        }
    }

    @Test
    // This test is a bit awkward, but it's a nice way to blackbox verify that all mappings exists
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
