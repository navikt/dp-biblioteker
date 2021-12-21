package no.nav.dagpenger.pdf

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import java.io.InputStream

class PdfTest {

    @Test
    fun `is not pdf`() {
        PDFDocument.load("sfasf".toByteArray()).shouldBeTypeOf<InvalidPDFDocument>()
    }

    @Test
    fun `is pdf`() {
        "/pdfs/minimal.pdf".asInputStream().use {
            with(PDFDocument.load(it.readBytes())) {
                this.shouldBeTypeOf<ValidPDFDocument>()
                signed shouldBe false
            }
        }
    }

    @Test
    fun `pdf is password protected`() {
        "/pdfs/protected.pdf".asInputStream().use {
            PDFDocument.load(it.readBytes()).shouldBeTypeOf<InvalidPDFDocument>()
        }
    }

    @Test
    fun `pdf is signed`() {
        "/pdfs/signed.pdf".asInputStream().use {
            with(PDFDocument.load(it.readBytes())) {
                this.shouldBeTypeOf<ValidPDFDocument>()
                signed shouldBe true
            }
        }
    }

    @Test
    fun `split document`() {
        "/pdfs/valid_with_5_pages.pdf".asInputStream().use {
            with(PDFDocument.load(it.readBytes())) {
                numberOfPages() shouldBe 5
                split().size shouldBe 5
                split(2).size shouldBe 3
                split(7).size shouldBe 1
            }
        }
    }

    private fun String.asInputStream(): InputStream {
        return object {}.javaClass.getResource(this)?.openStream()
            ?: throw FileNotFoundException()
    }
}
