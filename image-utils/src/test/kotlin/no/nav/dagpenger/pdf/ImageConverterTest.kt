package no.nav.dagpenger.pdf

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import no.nav.dagpenger.pdf.Detect.isPng
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.awt.Dimension
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
    fun `konverter og skalerer png til pdf`() {
        "/images/bilde.png".fileAsInputStream().use { inputstream ->
            PDFDocument.load(ImageConverter.toPDF(inputstream, Dimension(800, 600))).use { document ->
                document.shouldBeTypeOf<ValidPDFDocument>()
            }
        }
    }

    @Test
    fun `konverter og skalerer jpeg til pdf`() {
        "/images/bilde.jpeg".fileAsInputStream().use { inputStream ->
            PDFDocument.load(ImageConverter.toPDF(inputStream, Dimension(700, 400))).use { document ->
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
    fun `kan bare konvertere jpeg eller png til pdf`() {
        shouldThrow<IllegalArgumentException> { ImageConverter.toPDF("hbba".toByteArray()) }
    }

    @Test
    fun `konverter og skalerer jpeg til png`() {
        "/images/bilde.jpeg".fileAsByteArray().let {
            ImageConverter.toPNG(it, Dimension(500, 500)).also { image ->
                image.isPng() shouldBe true
            }
        }
    }

    @Test
    fun `konverter og skalerer png til png`() {
        "/images/bilde.jpeg".fileAsByteArray().let {
            ImageConverter.toPNG(it, Dimension(500, 500)).also { image ->
                image.isPng() shouldBe true
            }
        }
    }

    @Test
    fun `konverter og skalerer forste siden av en pdf til png`() {
        "/pdfs/valid_with_5_pages.pdf".fileAsByteArray().let {
            ImageConverter.toPNG(it, Dimension(500, 500)).also { image ->
                image.isPng() shouldBe true
            }
        }
    }

    @Test
    fun `Kan IKKE konvertere og skalere ugyldige pdf til png`() {
        shouldThrow<IllegalArgumentException> {
            "/pdfs/protected.pdf".fileAsByteArray().let {
                ImageConverter.toPNG(it, Dimension(500, 500)).also { image ->
                    image.isPng() shouldBe true
                }
            }
        }
    }

    @Test
    @Disabled
    fun `konverter og skalerer bilder og pdfs til png filer`() {
        "/images/bilde.png".fileAsByteArray().let {
            ImageConverter.toPNG(it, Dimension(600, 800)).let { png ->
                BufferedOutputStream(FileOutputStream("build/tmp/bilde_png_skalert.png")).use { os ->
                    os.write(png)
                }
            }
        }
        "/images/bilde.jpeg".fileAsByteArray().let {
            ImageConverter.toPNG(it, Dimension(600, 800)).let { png ->
                BufferedOutputStream(FileOutputStream("build/tmp/bilde_jpeg_skalert.png")).use { os ->
                    os.write(png)
                }
            }
        }

        "/pdfs/valid_with_5_pages.pdf".fileAsByteArray().let {
            ImageConverter.toPNG(it, Dimension(600, 800)).let { png ->
                BufferedOutputStream(FileOutputStream("build/tmp/pdf_skalert.png")).use { os ->
                    os.write(png)
                }
            }
        }
    }

    @Test
    @Disabled
    fun `konverter bilder til pdf filer`() {
        "/images/bilde.png".fileAsByteArray().let {
            PDFDocument.load(ImageConverter.toPDF(it)).use { pdf ->
                pdf.shouldBeTypeOf<ValidPDFDocument>()
                BufferedOutputStream(FileOutputStream("build/tmp/bilde_png.pdf")).use { os ->
                    pdf.save(os)
                }
            }
        }
        "/images/bilde.jpeg".fileAsByteArray().let {
            PDFDocument.load(ImageConverter.toPDF(it)).use { pdf ->
                pdf.shouldBeTypeOf<ValidPDFDocument>()
                BufferedOutputStream(FileOutputStream("build/tmp/bilde_jpeg.pdf")).use { os ->
                    pdf.save(os)
                }
            }
        }

        "/images/bilde.png".fileAsInputStream().use { inputstream ->
            PDFDocument.load(ImageConverter.toPDF(inputstream, Dimension(800, 600))).use { pdf ->
                pdf.shouldBeTypeOf<ValidPDFDocument>()
                BufferedOutputStream(FileOutputStream("build/tmp/bilde_png_skalert.pdf")).use { os ->
                    pdf.save(os)
                }
            }
        }

        "/images/bilde.jpeg".fileAsInputStream().use { inputstream ->
            PDFDocument.load(ImageConverter.toPDF(inputstream, Dimension(800, 600))).use { pdf ->
                pdf.shouldBeTypeOf<ValidPDFDocument>()
                BufferedOutputStream(FileOutputStream("build/tmp/bilde_jpeg_skalert.pdf")).use { os ->
                    pdf.save(os)
                }
            }
        }
    }
}
