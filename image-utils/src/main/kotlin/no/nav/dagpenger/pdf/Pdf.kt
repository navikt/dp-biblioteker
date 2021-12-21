package no.nav.dagpenger.pdf

import org.apache.pdfbox.multipdf.Splitter
import org.apache.pdfbox.pdmodel.PDDocument
import java.io.Closeable
import java.io.OutputStream

sealed class PDFDocument private constructor(protected val document: PDDocument) : Closeable {
    companion object {
        fun load(bytes: ByteArray): PDFDocument {
            return try {
                ValidPDFDocument(PDDocument.load(bytes))
            } catch (e: Exception) {
                InvalidPDFDocument(e)
            }
        }
    }

    open fun split(splitAtPage: Int = 1): List<PDFDocument> = emptyList()
    open fun save(outputStream: OutputStream): Unit = throw NotImplementedError()
    val signed: Boolean = this.document.signatureDictionaries.isEmpty().not()
    val encrypted: Boolean = this.document.isEncrypted
    fun numberOfPages(): Int = this.document.numberOfPages

    override fun close() {
        this.document.close()
    }
}

class InvalidPDFDocument(private val exception: Exception) : PDFDocument(PDDocument()) {
    fun message() = exception.message
}

class ValidPDFDocument(document: PDDocument) : PDFDocument(document) {
    override fun split(splitAtPage: Int): List<PDFDocument> {
        return Splitter().also { it.setSplitAtPage(splitAtPage) }.split(this.document).map { ValidPDFDocument(it) }

    }

    override fun save(outputStream: OutputStream) {
        this.document.save(outputStream)
    }
}