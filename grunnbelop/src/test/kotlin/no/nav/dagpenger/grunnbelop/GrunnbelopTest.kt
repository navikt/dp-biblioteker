package no.nav.dagpenger.grunnbelop

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forOne
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

class GrunnbelopTest {
    @Test
    fun ` Skal returnere grunnbeløp på 101351 måned mai 2021 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            dato = YearMonth.of(
                2021,
                Month.MAY
            ),
        ).verdi shouldBe 101351.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 101351 for måned mai 2020 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            dato = YearMonth.of(
                2020,
                Month.MAY
            ),
            gjeldendeDato = LocalDate.of(2020, 9, 21)
        ).verdi shouldBe 101351.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned april 2020 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2020,
                Month.APRIL
            )
        ).verdi shouldBe 99858.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 99858 for måned may 2019 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2019,
                Month.MAY
            )
        ).verdi shouldBe 99858.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 96883 for måned april 2019 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2019,
                Month.APRIL
            )
        ).verdi shouldBe 96883.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 93634 for måned mars 2018 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2018,
                Month.MARCH
            )
        ).verdi shouldBe 93634.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 92576 for måned mai 2016  `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2016,
                Month.MAY
            )
        ).verdi shouldBe 92576.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for måned august 2015 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forMåned(
            YearMonth.of(
                2015,
                Month.AUGUST
            )
        ).verdi shouldBe 90068.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for dato 06082015 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forDato(
            LocalDate.of(
                2015,
                8,
                6
            )
        ).verdi shouldBe 90068.toBigDecimal()
    }

    @Test
    fun ` Skal returnere grunnbeløp på 90068 for dato 01052019 `() {
        getGrunnbeløpForRegel(Regel.Grunnlag).forDato(
            LocalDate.of(
                2019,
                5,
                1
            )
        ).verdi shouldBe 99858.toBigDecimal()
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
    fun `Er det ikke definert noe gyldighetsperiode som varer lenge nok, skal den nyeste brukes`() {
        forAll(
            row(Regel.Minsteinntekt),
            row(Regel.Grunnlag)
        ) { regel ->
            val grunnbeløpForRegel = getGrunnbeløpForRegel(regel)
            grunnbeløpForRegel.forDato(
                LocalDate.of(
                    2099,
                    8,
                    6
                ),
                gjeldendeDato = LocalDate.of(2099, 9, 21)
            ).verdi shouldBe grunnbeløpForRegel.first().grunnbeløp.verdi
        }
    }

    @Test
    fun `Spør man for langt tilbake i tid skal man ikke få et grunnbeløp`() {
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
    fun `Vi kan legge til grunnbeløp som ikke trår i kraft før i framtiden`() {
        val gjustering = LocalDate.now().plusDays(10)
        val beregningIFramtidenFørPlanlagtGjustering = gjustering.minusDays(5)
        val beregningIFramtidenEtterPlanlagtGjustering = gjustering.plusDays(5)

        val mappingsMedFramtidigGjustering = setOf(
            GrunnbeløpPolicy(
                regel = Regel.Grunnlag,
                fom = gjustering.minusDays(20),
                iverksattFom = gjustering,
                grunnbeløp = Grunnbeløp.FastsattI2016
            ),
            GrunnbeløpPolicy(
                regel = Regel.Grunnlag,
                fom = LocalDate.of(2015, 5, 1),
                iverksattFom = LocalDate.of(2015, 5, 1),
                grunnbeløp = Grunnbeløp.FastsattI2015
            )
        )

        mappingsMedFramtidigGjustering.forDato(beregningIFramtidenFørPlanlagtGjustering) shouldBe Grunnbeløp.FastsattI2015
        mappingsMedFramtidigGjustering.forDato(beregningIFramtidenEtterPlanlagtGjustering) shouldBe Grunnbeløp.FastsattI2015
    }

    @Test
    fun `Grunnlag og minsteinntekt har uilke grunnbeløp før og etter gjustering`() {
        val gjusteringsDato = LocalDate.now().minusDays(5)
        val tilbakedatertBeregningsdatoEtterGjustering = gjusteringsDato.minusDays(10)

        val mappingsForGrunnlag = setOf(
            GrunnbeløpPolicy(
                regel = Regel.Grunnlag,
                fom = gjusteringsDato.minusDays(20),
                iverksattFom = gjusteringsDato,
                grunnbeløp = Grunnbeløp.FastsattI2016
            )
        )
        val mappingsForMinsteinntekt = setOf(
            GrunnbeløpPolicy(
                regel = Regel.Minsteinntekt,
                fom = gjusteringsDato,
                iverksattFom = gjusteringsDato,
                grunnbeløp = Grunnbeløp.FastsattI2016
            ),
            GrunnbeløpPolicy(
                regel = Regel.Minsteinntekt,
                fom = gjusteringsDato.minusYears(1),
                iverksattFom = gjusteringsDato.minusYears(1),
                grunnbeløp = Grunnbeløp.FastsattI2015
            )
        )

        val grunnbeløpForGrunnlag = mappingsForGrunnlag.forDato(tilbakedatertBeregningsdatoEtterGjustering)
        val grunnbeløpForMinsteinntekt =
            mappingsForMinsteinntekt.forDato(tilbakedatertBeregningsdatoEtterGjustering)

        assertSoftly {
            grunnbeløpForGrunnlag shouldBe Grunnbeløp.FastsattI2016
            grunnbeløpForMinsteinntekt shouldBe Grunnbeløp.FastsattI2015
        }
    }

    @Test
    fun `Alle grunnbeløp har en mapping`() {
        val iverksatteGrunnbeløp = Grunnbeløp.values().filter { it.iverksattFom.isBefore(LocalDate.now()) }

        iverksatteGrunnbeløp.forAll { grunnbeløp ->
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
        val iverksatteGrunnbeløp = Grunnbeløp.values().filter { it.iverksattFom.isBefore(LocalDate.now()) }

        iverksatteGrunnbeløp.forAll { beløp ->
            Regel.values().forAll { regel ->
                val grunnbeløpForRegel = getGrunnbeløpForRegel(regel)
                grunnbeløpForRegel.shouldBeInstanceOf<Set<GrunnbeløpPolicy>>()

                grunnbeløpForRegel.forOne {
                    it.grunnbeløp shouldBe beløp
                }
            }
        }
    }
}
