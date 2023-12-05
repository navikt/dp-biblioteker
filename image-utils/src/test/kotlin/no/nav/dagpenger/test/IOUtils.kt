package no.nav.dagpenger.pdf

import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

internal fun String.fileAsInputStream(): InputStream {
    return object {}.javaClass.getResource(this)?.openStream()
        ?: throw FileNotFoundException()
}

internal fun String.fileAsBufferedInputStream(): BufferedInputStream = this.fileAsInputStream().buffered()

internal fun String.fileAsByteArray(): ByteArray = this.fileAsInputStream().use { it.readAllBytes() }

internal fun ByteArray.extractText(): String {
    return Loader.loadPDF(this).use {
        PDFTextStripper().getText(it)
    }
}

internal fun ByteArray.writeToFile(filename: String) {
    BufferedOutputStream(FileOutputStream(filename)).use {
        it.write(this)
    }
}
