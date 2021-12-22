package no.nav.dagpenger.pdf

import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import org.junit.jupiter.api.Test
import java.io.BufferedOutputStream
import java.io.FileOutputStream

internal class ImageConverterTest {

    @Test
    fun `konverter ikke pdf`() {
        "/pdfs/minimal.pdf".fileAsByteArray().let {
            Image.toPDF(it) shouldBeSameInstanceAs it
        }
    }

    @Test
    fun `konverter png til pdf`() {
        "/images/bilde.png".fileAsByteArray().let {
            PDFDocument.load(Image.toPDF(it)).shouldBeTypeOf<ValidPDFDocument>()
        }
    }

    @Test
    fun `konverter jpg til pdf`() {
        "/images/bilde.jpeg".fileAsByteArray().let {
            PDFDocument.load(Image.toPDF(it)).shouldBeTypeOf<ValidPDFDocument>()
        }
    }

    @Test
//    @Disabled("Manuell verifkasjon test som skriver til filsystem")
    fun `konverter og skriv til filsystem`() {
        "/images/bilde.png".fileAsByteArray().let {
            PDFDocument.load(Image.toPDF(it)).shouldBeTypeOf<ValidPDFDocument>().also { document ->
                document.save(BufferedOutputStream(FileOutputStream("bilde_png.pdf")))
            }
        }
        "/images/bilde.jpeg".fileAsByteArray().let {
            PDFDocument.load(Image.toPDF(it)).shouldBeTypeOf<ValidPDFDocument>().also { document ->
                document.save(BufferedOutputStream(FileOutputStream("bilde_jepg.pdf")))
            }
        }
    }
}
