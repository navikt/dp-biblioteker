package no.nav.dagpenger.pdf

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import no.nav.dagpenger.io.Detect.isPdf
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class PdfWatermarkerTest {
    private val mockFnr = "12345678910"
    private val write = true

    @Test
    fun `Vannmarker PDF med dato`() {
        val now = LocalDateTime.of(2013, 3, 18, 14, 30, 30)
        "/pdfs/overgangsstonadskjema.pdf".fileAsByteArray().let { originalPdf ->
            PdfWatermarker.applyOn(originalPdf, mockFnr, now).also { watermarkedBytes ->
                watermarkedBytes.isPdf() shouldBe true
                watermarkedBytes shouldNotBe originalPdf
                if (write) {
                    watermarkedBytes.writeToFile("build/tmp/overgangsstonadskjema_wm_dato.pdf")
                }
            }.extractText().also { text ->
                text shouldContain PdfWatermarker.LINE_1_HEADER
                text shouldContain PdfWatermarker.LINE_2_HEADER
                text shouldContain mockFnr
                text shouldContain "18.03.2013, kl. 14:30:30"
            }
        }
    }

    @Test
    fun `Vannmarker PDF uten dato`() {
        "/pdfs/overgangsstonadskjema.pdf".fileAsByteArray().let { originalPdf ->
            PdfWatermarker.applyOn(originalPdf, mockFnr, null).also { watermarkedBytes ->
                watermarkedBytes.isPdf() shouldBe true
                watermarkedBytes shouldNotBe originalPdf
                if (write) {
                    watermarkedBytes.writeToFile("build/tmp/overgangsstonadskjema_wm_uten_dato.pdf")
                }
            }.extractText().also { text ->
                text shouldContain PdfWatermarker.LINE_1_HEADER
                text shouldContain PdfWatermarker.LINE_2_HEADER
                text shouldContain mockFnr
                text shouldNotContain "18.03.2013, kl. 14:30:30"
            }
        }
    }

    @Test
    fun `exception hvis ikke pdf`() {
        shouldThrow<IllegalArgumentException> {
            "/pdfs/fake_pdf.pdf".fileAsByteArray().let {
                PdfWatermarker.applyOn(it, "hubba", null)
            }
        }
    }
}
