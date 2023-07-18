package no.nav.dagpenger.io

import io.kotest.matchers.shouldBe
import no.nav.dagpenger.io.Detect.isImage
import no.nav.dagpenger.io.Detect.isJpeg
import no.nav.dagpenger.io.Detect.isPdf
import no.nav.dagpenger.io.Detect.isPng
import no.nav.dagpenger.pdf.fileAsBufferedInputStream
import no.nav.dagpenger.pdf.fileAsByteArray
import org.junit.jupiter.api.Test

class DetectTest {

    @Test
    fun `detect pdf`() {
        "/pdfs/minimal.pdf".fileAsBufferedInputStream().use {
            it.isPdf() shouldBe true
        }

        "/pdfs/fake_pdf.pdf".fileAsBufferedInputStream().use {
            it.isPdf() shouldBe false
        }
    }

    @Test
    fun `detect list of byte arrays representing pdfs`() {
        val legalPdf = "/pdfs/minimal.pdf".fileAsByteArray()
        val illegalPdf = "/pdfs/fake_pdf.pdf".fileAsByteArray()

        listOf(legalPdf, legalPdf).isPdf() shouldBe true

        listOf(legalPdf).isPdf() shouldBe true

        emptyList<ByteArray>().isPdf() shouldBe false
        listOf(illegalPdf).isPdf() shouldBe false
        listOf(legalPdf, illegalPdf).isPdf() shouldBe false
    }

    @Test
    fun `detect png`() {
        "/images/bilde.png".fileAsBufferedInputStream().use {
            it.isPng() shouldBe true
        }

        "/images/fake_png.png".fileAsBufferedInputStream().use {
            it.isPng() shouldBe false
        }
    }

    @Test
    fun `detect jpg`() {
        "/images/bilde.jpeg".fileAsBufferedInputStream().use {
            it.isJpeg() shouldBe true
        }

        "/images/fake_jpeg.jpeg".fileAsBufferedInputStream().use {
            it.isJpeg() shouldBe false
        }
    }

    @Test
    fun `detect image`() {
        "/images/bilde.jpeg".fileAsBufferedInputStream().use {
            it.isImage() shouldBe true
        }

        "/images/bilde.jpeg".fileAsBufferedInputStream().use {
            it.isImage() shouldBe true
        }
    }
}
