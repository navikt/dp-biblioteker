package no.nav.dagpenger.pdf

import io.kotest.matchers.shouldBe
import no.nav.dagpenger.pdf.Detect.isJpeg
import no.nav.dagpenger.pdf.Detect.isPdf
import no.nav.dagpenger.pdf.Detect.isPng
import org.junit.jupiter.api.Test
import java.io.BufferedInputStream
import java.io.FileNotFoundException

class DetectTest {

    @Test
    fun `detect pdf`() {
        "/pdfs/minimal.pdf".asBufferedInputStream().use {
            it.isPdf() shouldBe true
        }

        "/pdfs/fake_pdf.pdf".asBufferedInputStream().use {
            it.isPdf() shouldBe false
        }
    }

    @Test
    fun `detect png`() {
        "/images/bilde.png".asBufferedInputStream().use {
            it.isPng() shouldBe true
        }

        "/images/fake_png.png".asBufferedInputStream().use {
            it.isPng() shouldBe false
        }
    }

    @Test
    fun `detect jpg`() {
        "/images/bilde.jpg".asBufferedInputStream().use {
            it.isJpeg() shouldBe true
        }

        "/images/fake_jpg.jpg".asBufferedInputStream().use {
            it.isJpeg() shouldBe false
        }
    }

    private fun String.asBufferedInputStream(): BufferedInputStream {
        return object {}.javaClass.getResource(this)?.openStream()?.buffered()
            ?: throw FileNotFoundException()
    }
}
