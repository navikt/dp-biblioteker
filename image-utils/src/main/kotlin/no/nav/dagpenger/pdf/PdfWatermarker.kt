package no.nav.dagpenger.pdf

import no.nav.dagpenger.io.Detect.isPdf
import org.apache.pdfbox.cos.COSDictionary
import org.apache.pdfbox.cos.COSName
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState
import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Vannmerker PDF
 *
 *
 * For flersidig PDF legges vannmerket på samtlige sider.
 */
object PdfWatermarker {
    const val LINE_1_HEADER = "Sendt elektronisk"
    const val LINE_2_HEADER = "Fødselsnummer: "
    private const val MARGIN = 5
    private const val PADDING_X = 5
    private const val PADDING_Y = 5
    private const val LINE_SPACING = 5
    private const val LINJE1_START_Y = 13
    private const val LINJE2_START_Y = 5
    private const val BACKGROUND_OPACITY = 0.8f
    private const val DEFAULT_BACKGROUND_OPACITY = 1.0f
    private const val BORDER_WIDTH = 1f
    private const val HEIGHT = 22f
    private val FONT: PDFont = PDType1Font.HELVETICA
    private const val FONT_SIZE = 6

    private fun removePermissions(pdfDocument: PDDocument) {
        val cosDictionary = pdfDocument.document.trailer?.getDictionaryObject(COSName.ROOT)?.let { it as COSDictionary }
        cosDictionary
            ?.keySet()
            ?.singleOrNull { it.name == "Perm" }
            ?.let { cosDictionary.removeItem(it) }
    }

    private data class Placement(val x: Float, val y: Float, val width: Float, val height: Float)

    private fun calculateWaterMarkPlacement(
        page: PDPage,
        linje1: String,
        linje2: String,
    ): Placement {
        val dimensions: PDRectangle = page.mediaBox ?: page.trimBox
        val pageHeight: Float = dimensions.getHeight()
        val pageWidth: Float = dimensions.getWidth()
        val lineWidth =
            Math.max(
                findLineWidthForTextWithFontSize(linje1, FONT, FONT_SIZE),
                findLineWidthForTextWithFontSize(linje2, FONT, FONT_SIZE),
            ) + PADDING_X + PADDING_Y
        val upperRightX = pageWidth - MARGIN
        val upperRightY = pageHeight - MARGIN
        val lowerLeftX = upperRightX - lineWidth - 2 * PADDING_X
        val lowerLeftY = upperRightY - 2 * FONT_SIZE - 2 * PADDING_Y - LINE_SPACING - 2 * BORDER_WIDTH

        return Placement(lowerLeftX, lowerLeftY, lineWidth, HEIGHT)
    }

    private fun stampRectangleOnPdf(
        pdfDocument: PDDocument,
        linje1: String,
        linje2: String,
    ) {
        for (page: PDPage in pdfDocument.documentCatalog.pages) {
            val (lowerLeftX, lowerLeftY, lineWidth, height) = calculateWaterMarkPlacement(page, linje1, linje2)
            setOpacity(page.resources, BACKGROUND_OPACITY)
            try {
                PDPageContentStream(
                    pdfDocument,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true,
                ).use { it: PDPageContentStream ->
                    it.setNonStrokingColor(Color.white)
                    it.addRect(lowerLeftX, lowerLeftY, lineWidth, height)
                    it.fill()
                }
            } catch (e: IOException) {
                throw RuntimeException(e.message, e)
            }
        }
    }

    private fun stampTextOnPdf(
        pdfDocument: PDDocument,
        linje1: String,
        linje2: String,
    ) {
        for (page in pdfDocument.documentCatalog.pages) {
            val (lowerLeftX, lowerLeftY, lineWidth, height) = calculateWaterMarkPlacement(page, linje1, linje2)
            setOpacity(page.getResources(), DEFAULT_BACKGROUND_OPACITY)
            try {
                PDPageContentStream(
                    pdfDocument,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true,
                ).use { pdPage ->
                    pdPage.setFont(FONT, FONT_SIZE.toFloat())
                    pdPage.setNonStrokingColor(Color.black)
                    pdPage.beginText()
                    pdPage.newLineAtOffset(lowerLeftX + MARGIN, lowerLeftY + LINJE1_START_Y)
                    pdPage.showText(linje1)
                    pdPage.endText()
                    pdPage.beginText()
                    pdPage.newLineAtOffset(lowerLeftX + MARGIN, lowerLeftY + LINJE2_START_Y)
                    pdPage.showText(linje2)
                    pdPage.endText()
                    pdPage.setStrokingColor(Color.red)
                    pdPage.setLineWidth(BORDER_WIDTH)
                    pdPage.moveTo(lowerLeftX, lowerLeftY)
                    pdPage.lineTo(lowerLeftX, lowerLeftY + height)
                    pdPage.moveTo(lowerLeftX, lowerLeftY + height)
                    pdPage.lineTo(lowerLeftX + lineWidth, lowerLeftY + height)
                    pdPage.moveTo(lowerLeftX + lineWidth, lowerLeftY + height)
                    pdPage.lineTo(lowerLeftX + lineWidth, lowerLeftY)
                    pdPage.moveTo(lowerLeftX + lineWidth, lowerLeftY)
                    pdPage.lineTo(lowerLeftX, lowerLeftY)
                }
            } catch (e: IOException) {
                throw RuntimeException(e.message, e)
            }
        }
    }

    private fun setOpacity(
        resources: PDResources,
        opacity: Float,
    ) {
        val graphicsState = PDExtendedGraphicsState()
        graphicsState.setNonStrokingAlphaConstant(opacity)
        resources.add(graphicsState)
    }

    private fun formatertDato(date: LocalDateTime): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy', kl. 'HH:mm:ss")
        return date.format(dateTimeFormatter)
    }

    private fun findLineWidthForTextWithFontSize(
        text: String,
        font: PDFont,
        fontSize: Int,
    ): Float {
        return font.getStringWidth(text) / 1000 * fontSize
    }

    fun applyOn(
        bytes: ByteArray,
        ident: String,
        includeDate: LocalDateTime?,
    ): ByteArray {
        require(bytes.isPdf()) { "Kan kun vannmerke PDF-filer." }

        return PDDocument.load(ByteArrayInputStream(bytes)).use { pdfDocument ->
            applyOn(pdfDocument, ident, includeDate)
        }
    }

    fun applyOn(
        pdfDocument: PDDocument,
        ident: String,
        includeDate: LocalDateTime?,
    ): ByteArray {
        val linje1 = if (includeDate == null) LINE_1_HEADER else LINE_1_HEADER + ": ${formatertDato(includeDate)}"
        val linje2 = LINE_2_HEADER + ident

        removePermissions(pdfDocument)
        stampRectangleOnPdf(pdfDocument, linje1, linje2)
        stampTextOnPdf(pdfDocument, linje1, linje2)

        return ByteArrayOutputStream().also { os ->
            pdfDocument.save(os)
        }.toByteArray()
    }
}
