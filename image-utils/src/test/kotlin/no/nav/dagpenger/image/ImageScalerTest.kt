package no.nav.dagpenger.image

import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.pdf.ImageScaler
import no.nav.dagpenger.pdf.ImageScaler.ScaleMode.CROP_TO_FILL_ENTIRE_BOX
import no.nav.dagpenger.pdf.ImageScaler.ScaleMode.SCALE_TO_FIT_INSIDE_BOX
import no.nav.dagpenger.pdf.fileAsBufferedInputStream
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream
import kotlin.math.abs

internal class ImageScalerTest {
    @Test
    fun `Detect images`() {
        shouldThrow<IllegalArgumentException> {
            "/pdfs/minimal.pdf".fileAsBufferedInputStream().use {
                ImageScaler.scale(it, Dimension(100, 100), SCALE_TO_FIT_INSIDE_BOX)
            }
        }
    }

    @Test
    fun `Can scale image to fit inside a box`() {
        val boundingBox = Dimension(100, 150)

        "/images/lfc.png".fileAsBufferedInputStream().use {
            val scaledImage = ImageScaler.scale(it, boundingBox, SCALE_TO_FIT_INSIDE_BOX)
            scaledImage fitsInside boundingBox shouldBe true
        }
    }

    @Test
    fun `Scaled image keeps aspect ratio`() {
        val boundingBox = Dimension(100, 150)
        val originalImage = "/images/lfc.png".fileAsBufferedInputStream().use { ImageIO.read(it) }
        val scaledImage = ImageScaler.scale(originalImage, boundingBox, SCALE_TO_FIT_INSIDE_BOX)

        val scaleFactorWidth = scaledImage.width.toDouble() / originalImage.width
        val scaleFactorHeight = scaledImage.height.toDouble() / originalImage.height

        scaleFactorWidth shouldBe (scaleFactorHeight plusOrMinus 0.01)
    }

    @Test
    fun `crop image to fill box maintains dimensions`() {
        val boundingBox = Dimension(50, 75)

        val originalImage = "/images/lfc.png".fileAsBufferedInputStream().use { ImageIO.read(it) }
        val croppedImage = ImageScaler.scale(originalImage, boundingBox, CROP_TO_FILL_ENTIRE_BOX)

        croppedImage.width shouldBe boundingBox.width
        croppedImage.height shouldBe boundingBox.height
    }

    @Test
    fun `crop image returns image with right dimensions`() {
        val boundingBox = Dimension(50, 75)

        val originalImage = "/images/lfc.png".fileAsBufferedInputStream().use { ImageIO.read(it) }
        val croppedImage = ImageScaler.crop(originalImage, boundingBox)

        croppedImage.width shouldBe boundingBox.width
        croppedImage.height shouldBe boundingBox.height
    }

    @Test
    fun `Image can not be smaller than box`() {
        val originalImage = "/images/lfc.png".fileAsBufferedInputStream().use { ImageIO.read(it) }

        shouldThrow<IllegalArgumentException> {
            ImageScaler.crop(originalImage, Dimension(originalImage.width - 10, originalImage.height + 10))
        }

        shouldThrow<IllegalArgumentException> {
            ImageScaler.crop(originalImage, Dimension(originalImage.width + 10, originalImage.height - 10))
        }

        shouldNotThrowAnyUnit {
            ImageScaler.crop(originalImage, Dimension(originalImage.width - 10, originalImage.height - 10))
        }
    }

    @Test
    @Disabled
    fun `Manual test`() {
        val boundingBox = Dimension(50, 75)
        val originalImage = "/images/lfc.png".fileAsBufferedInputStream().use { ImageIO.read(it) }
        FileImageOutputStream(File("build/tmp/lfc_scaled.png")).use { os ->
            ImageIO.write(ImageScaler.scale(originalImage, boundingBox, SCALE_TO_FIT_INSIDE_BOX), "png", os)
        }

        FileImageOutputStream(File("build/tmp/lfc_cropped.png")).use { os ->
            ImageIO.write(ImageScaler.scale(originalImage, boundingBox, CROP_TO_FILL_ENTIRE_BOX), "png", os)
        }
    }

    private infix fun BufferedImage.fitsInside(box: Dimension): Boolean {
        val epsilon = 0.01
        val widthMatches: Boolean =
            abs(this.width - box.width) < epsilon
        val heightMatches: Boolean =
            abs(this.height - box.height) < epsilon
        return widthMatches || heightMatches
    }
}
