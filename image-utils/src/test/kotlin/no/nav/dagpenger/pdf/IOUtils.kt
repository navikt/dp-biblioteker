package no.nav.dagpenger.pdf

import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.InputStream

internal fun String.fileAsInputStream(): InputStream {
    return object {}.javaClass.getResource(this)?.openStream()
        ?: throw FileNotFoundException()
}

internal fun String.fileAsBufferedInputStream(): BufferedInputStream = this.fileAsInputStream().buffered()

internal fun String.fileAsByteArray(): ByteArray = this.fileAsInputStream().use { it.readAllBytes() }
