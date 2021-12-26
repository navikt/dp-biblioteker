package no.nav.dagpenger.pdf

import org.apache.pdfbox.multipdf.Splitter
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import java.awt.image.BufferedImage
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

sealed class PDFDocument private constructor(val document: PDDocument) : Closeable {
    companion object {
        fun load(bytes: ByteArray): PDFDocument {
            return try {
                ValidPDFDocument(PDDocument.load(bytes))
            } catch (e: Exception) {
                InvalidPDFDocument(e)
            }
        }

        fun load(inputStream: InputStream): PDFDocument {
            return try {
                ValidPDFDocument(PDDocument.load(inputStream.buffered()))
            } catch (e: Exception) {
                InvalidPDFDocument(e)
            }
        }
    }

    fun numberOfPages(): Int = this.document.numberOfPages

    fun split(splitAtPage: Int = 1): List<PDFDocument> {
        require(numberOfPages() > 0) {
            "Document must have a least one page."
        }
        return Splitter().also { it.setSplitAtPage(splitAtPage) }.split(this.document).map { ValidPDFDocument(it) }
    }

    open fun save(outputStream: OutputStream) {
        require(numberOfPages() > 0) {
            "Document must have a least one page."
        }
        this.document.save(outputStream)
    }

    val signed: Boolean = this.document.signatureDictionaries.isEmpty().not()
    val encrypted: Boolean = this.document.isEncrypted

    fun convertToImage(pageIndex: Int): BufferedImage {
        require(numberOfPages() > pageIndex) {
            "Document has only ${numberOfPages()} pages"
        }
        return PDFRenderer(this.document).renderImage(pageIndex)
    }

    override fun close() {
        this.document.close()
    }
}

class InvalidPDFDocument(private val exception: Exception) : PDFDocument(PDDocument()) {
    fun message() = exception.message
}

class ValidPDFDocument(document: PDDocument) : PDFDocument(document)
