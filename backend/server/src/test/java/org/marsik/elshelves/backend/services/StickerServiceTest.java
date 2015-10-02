package org.marsik.elshelves.backend.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class StickerServiceTest {
    @Test
    public void testWorkingUnicodePDF() throws Exception {
        PDDocument document = new PDDocument();

        PDFont titleFont = PDType0Font.load(document, this.getClass().getResourceAsStream("/org/marsik/elshelves/backend/fonts/DejaVuSans-Bold.ttf"));

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        contentStream.setFont(titleFont, 12);
        contentStream.newLineAtOffset(0, 100);
        contentStream.showText("Pěkný žluťoučký kůň úpěl ďábelské ódy");
        contentStream.endText();

        contentStream.close();

        document.close();

        assertNotNull(document);
    }

    @Test
    public void testFailingUnicodePDF() throws Exception {
        PDDocument document = new PDDocument();

        PDFont titleFont = PDType0Font.load(document, this.getClass().getResourceAsStream("/org/marsik/elshelves/backend/fonts/DejaVuSans-Bold.ttf"));

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        contentStream.setFont(titleFont, 12);
        contentStream.newLineAtOffset(0, 100);
        contentStream.showText("Pěkný žluťoučký kůň úpěl ďábelské ódy");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(titleFont, 12);
        contentStream.newLineAtOffset(0, 200);
        contentStream.showText("Pěkný žluťoučký kůň úpěl ďábelské ódy");
        contentStream.endText();

        contentStream.close();

        document.close();

        assertNotNull(document);
    }
}