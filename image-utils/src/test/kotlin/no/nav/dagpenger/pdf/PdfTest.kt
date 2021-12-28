package no.nav.dagpenger.pdf

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.OutputStream
import java.time.LocalDateTime

class PdfTest {

    @Test
    fun `is not pdf`() {
        PDFDocument.load("sfasf".toByteArray()).use { invalidPdf ->
            invalidPdf.shouldBeTypeOf<InvalidPDFDocument>()
            assertThrows<IllegalArgumentException> { invalidPdf.convertToImage(0) }
            assertThrows<IllegalArgumentException> { invalidPdf.split() }
            OutputStream.nullOutputStream().use { os ->
                assertThrows<IllegalArgumentException> { invalidPdf.save(os) }
            }
        }
    }

    @Test
    fun `is pdf`() {
        "/pdfs/minimal.pdf".fileAsInputStream().use {
            PDFDocument.load(it).use { pdf ->
                pdf.shouldBeTypeOf<ValidPDFDocument>()
                pdf.signed shouldBe false
            }
        }
    }

    @Test
    fun `pdf is password protected`() {
        "/pdfs/protected.pdf".fileAsInputStream().use {
            PDFDocument.load(it).use { pdf ->
                pdf.shouldBeTypeOf<InvalidPDFDocument>()
            }
        }
    }

    @Test
    fun `pdf is signed`() {
        "/pdfs/signed.pdf".fileAsInputStream().use {
            PDFDocument.load(it).use { pdf ->
                pdf.shouldBeTypeOf<ValidPDFDocument>()
                pdf.signed shouldBe true
            }
        }
    }

    @Test
    fun `split document`() {
        "/pdfs/valid_with_5_pages.pdf".fileAsInputStream().use {
            PDFDocument.load(it).use { pdf ->
                with(pdf) {
                    numberOfPages() shouldBe 5
                    split().size shouldBe 5
                    split(2).size shouldBe 3
                    split(7).size shouldBe 1
                }
            }
        }
    }

    @Test
    fun `merge documents`() {
        val pages: List<ByteArray> = listOf(
            "/pdfs/minimal.pdf".fileAsByteArray(),
            "/pdfs/minimal.pdf".fileAsByteArray()
        )

        PDFDocument.merge(pages).also {
            it.shouldBeTypeOf<ValidPDFDocument>()
            it.numberOfPages() shouldBe 2
        }
    }

    @Test
    fun `convert page to image`() {
        "/pdfs/valid_with_5_pages.pdf".fileAsInputStream().use {
            PDFDocument.load(it).use { pdf ->
                shouldNotThrowAny { pdf.convertToImage(4) }
                shouldThrow<IllegalArgumentException> { pdf.convertToImage(5) }
            }
        }
    }

    @Test
    fun `watermark pdf`() {
        "/pdfs/valid_with_5_pages.pdf".fileAsInputStream().use {
            PDFDocument.load(it).use { pdf ->
                shouldNotThrowAny {
                    pdf.waterMark("12345", LocalDateTime.now())
                }
            }
        }

        PDFDocument.load("byasf".toByteArray()).use {
            shouldThrow<IllegalArgumentException> {
                it.waterMark("12345", LocalDateTime.now())
            }
        }
    }
}
