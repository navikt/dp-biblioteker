package no.nav.dagpenger.pdf

import no.nav.dagpenger.pdf.Detect.isImage
import no.nav.dagpenger.pdf.Detect.isPdf
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.ByteArrayOutputStream

object Image {

    fun toPDF(input: ByteArray): ByteArray {
        if (input.isPdf()) return input
        require(input.isImage()) { "Only jpg and png is supported" }

        return ByteArrayOutputStream().use { os ->
            PDDocument().use { document ->
                val image = PDImageXObject.createFromByteArray(document, input, null)
                val page = PDPage(PDRectangle(image.width.toFloat(), image.height.toFloat()))
                document.addPage(page)

                PDPageContentStream(document, page).use {
                    it.drawImage(image, 0F, 0F)
                }
                document.save(os)
            }
            os.toByteArray()
        }
    }
}
