package no.nav.dagpenger.io

import org.apache.tika.Tika
import java.io.InputStream

object Detect {
    const val IMAGE_PNG = "image/png"
    const val IMAGE_JPEG = "image/jpeg"
    const val APPLICATON_PDF = "application/pdf"
    private val tika: Tika = Tika()

    fun InputStream.detect(): String {
        return tika.detect(this.buffered())
    }

    fun ByteArray.detect(): String = tika.detect(this)

    fun InputStream.isPng(): Boolean = this.detect() == IMAGE_PNG

    fun InputStream.isJpeg(): Boolean = this.detect() == IMAGE_JPEG

    fun InputStream.isPdf(): Boolean {
        return this.detect() == APPLICATON_PDF
    }

    fun ByteArray.isPng(): Boolean = this.detect() == IMAGE_PNG

    fun ByteArray.isJpeg(): Boolean = this.detect() == IMAGE_JPEG

    fun ByteArray.isPdf(): Boolean = this.detect() == APPLICATON_PDF

    fun InputStream.isImage(): Boolean = this.isJpeg() || this.isPng()

    fun ByteArray.isImage(): Boolean = this.isJpeg() || this.isPng()

    fun List<ByteArray>.isPdf(): Boolean {
        return this.isNotEmpty() && this.all { it.isPdf() }
    }
}
