package no.nav.dagpenger.pdf

import org.apache.tika.Tika
import java.io.InputStream

object Detect {
    private val tika: Tika = Tika()

    fun InputStream.detect(): String = tika.detect(this.buffered())

    fun InputStream.isPng(): Boolean = this.detect() == "image/png"
    fun InputStream.isJpeg(): Boolean = this.detect() == "image/jpeg"
    fun InputStream.isPdf(): Boolean = this.detect() == "application/pdf"

    fun InputStream.isImage(): Boolean = this.isJpeg() || this.isPng()
}
