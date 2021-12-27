package no.nav.dagpenger.pdf

import no.nav.dagpenger.pdf.Detect.isImage
import no.nav.dagpenger.pdf.Detect.isPdf
import no.nav.dagpenger.pdf.ImageConverter.requireImage
import no.nav.dagpenger.pdf.ImageScaler.ScaleMode.SCALE_TO_FIT_INSIDE_BOX
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.imgscalr.Scalr
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

object ImageConverter {

    internal fun requireImage(input: ByteArray) {
        require(input.isImage()) { "Only jpg and png is supported" }
    }

    internal fun requireImage(input: InputStream) {
        require(input.isImage()) { "Only jpg and png is supported" }
    }

    fun toPDF(input: ByteArray): ByteArray {
        if (input.isPdf()) return input
        requireImage(input)

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

    fun toPDF(input: InputStream, dimension: Dimension): ByteArray {
        requireImage(input)
        return ImageScaler.scale(input, dimension, SCALE_TO_FIT_INSIDE_BOX).toByteArray().let { toPDF(it) }
    }

    private fun BufferedImage.toByteArray(): ByteArray {
        return ByteArrayOutputStream().use { os ->
            ImageIO.write(this, "png", os)
            this.flush()
            os.toByteArray()
        }
    }

//    fun toPng(input: ByteArray, dimension: Dimension): ByteArray {
//        require(input.isImage() || input.isPdf()) {
//            "Kan kun konvertere PDF, JPG og PNG til PNG."
//        }
//
//        return if (input.isPdf()) {
//            PDFDocument.load(input)
//
//        } else {
//                ImageScaler.scale(input, dimension, SCALE_TO_FIT_INSIDE_BOX).let { image ->
//                    ImageIO.write(image, "png", os)
//                    image.flush()
//                    os.toByteArray()
//                }
//            }
//
//        }
//    }
}

object ImageScaler {
    enum class ScaleMode {
        SCALE_TO_FIT_INSIDE_BOX,
        CROP_TO_FILL_ENTIRE_BOX
    }

    fun scale(input: ByteArray, dimension: Dimension, scaleMode: ScaleMode): BufferedImage {
        requireImage(input)
        return ByteArrayInputStream(input).use { stream ->
            scale(stream, dimension, scaleMode)
        }
    }

    fun scale(input: InputStream, dimension: Dimension, scaleMode: ScaleMode): BufferedImage {
        requireImage(input)
        return scale(ImageIO.read(input), dimension, scaleMode)
    }

    fun scale(input: BufferedImage, dimension: Dimension, scaleMode: ScaleMode): BufferedImage {
        val scaleFactorWidth: Double = dimension.getWidth() / input.width
        val scaleFactorHeight: Double = dimension.getHeight() / input.height

        val scalingFactor = when (scaleMode) {
            SCALE_TO_FIT_INSIDE_BOX -> min(scaleFactorWidth, scaleFactorHeight)
            ScaleMode.CROP_TO_FILL_ENTIRE_BOX -> max(scaleFactorWidth, scaleFactorHeight)
        }

        val scaledImage = Scalr.resize(
            input,
            (scalingFactor * input.width).toInt(),
            (scalingFactor * input.height).toInt()
        )

        return when (scaleMode) {
            SCALE_TO_FIT_INSIDE_BOX -> scaledImage
            ScaleMode.CROP_TO_FILL_ENTIRE_BOX -> crop(scaledImage, dimension)
        }
    }

    fun crop(image: BufferedImage, dimension: Dimension): BufferedImage {
        require(image.width >= dimension.width && image.height >= dimension.height) {
            "Image must be at least as big as dimension"
        }

        val widthDelta = image.width - dimension.width
        val heightDelta = image.height - dimension.height

        return image.getSubimage(widthDelta / 2, heightDelta / 2, dimension.width, dimension.height)
    }
}
