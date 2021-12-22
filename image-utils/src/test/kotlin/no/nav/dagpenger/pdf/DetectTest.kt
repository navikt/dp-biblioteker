package no.nav.dagpenger.pdf

import io.kotest.matchers.shouldBe
import no.nav.dagpenger.pdf.Detect.isImage
import no.nav.dagpenger.pdf.Detect.isJpeg
import no.nav.dagpenger.pdf.Detect.isPdf
import no.nav.dagpenger.pdf.Detect.isPng
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
