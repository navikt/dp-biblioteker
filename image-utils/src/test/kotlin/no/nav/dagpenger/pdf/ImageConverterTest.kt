package no.nav.dagpenger.pdf

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.BufferedOutputStream
import java.io.FileOutputStream

internal class ImageConverterTest {

    @Test
    fun `konverter ikke pdf`() {
        "/pdfs/minimal.pdf".fileAsByteArray().let {
            ImageConverter.toPDF(it) shouldBeSameInstanceAs it
        }
    }

    @Test
    fun `konverter png til pdf`() {
        "/images/bilde.png".fileAsByteArray().let {
            PDFDocument.load(ImageConverter.toPDF(it)).use { document ->
                document.shouldBeTypeOf<ValidPDFDocument>()
            }
        }
    }

    @Test
    fun `konverter jpeg til pdf`() {
        "/images/bilde.jpeg".fileAsByteArray().let {
            PDFDocument.load(ImageConverter.toPDF(it)).use { document ->
                document.shouldBeTypeOf<ValidPDFDocument>()
            }
        }
    }

    @Test
    fun `kan bare konvertere jpeg eller png`() {
        shouldThrow<IllegalArgumentException> { ImageConverter.toPDF("hbba".toByteArray()) }
    }

    @Test
    @Disabled
    fun `konverter og skriv til filsystem`() {
        "/images/bilde.png".fileAsByteArray().let {
            PDFDocument.load(ImageConverter.toPDF(it)).shouldBeTypeOf<ValidPDFDocument>().also { document ->
                BufferedOutputStream(FileOutputStream("/build/tmp/bilde_png.pdf")).use { os ->
                    document.save(os)
                }
            }
        }
        "/images/bilde.jpeg".fileAsByteArray().let {
            PDFDocument.load(ImageConverter.toPDF(it)).shouldBeTypeOf<ValidPDFDocument>().also { document ->
                BufferedOutputStream(FileOutputStream("/build/tmp/bilde_jpeg.pdf")).use { os ->
                    document.save(os)
                }
            }
        }
    }
}
