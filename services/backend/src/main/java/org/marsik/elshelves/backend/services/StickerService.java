package org.marsik.elshelves.backend.services;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.marsik.elshelves.backend.dtos.StickerSettings;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class StickerService {
    public static class Result {
        final PDDocument document;

        public Result(PDDocument document) {
            this.document = document;
        }

        public void save(OutputStream os) {
            try {
                document.save(os);
            } catch (COSVisitorException|IOException ex) {
                ex.printStackTrace();
            }
        }

        public void close() {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final float MM_TO_UNITS = 1/(10*2.54f)*72;

    public Result generateStickers(StickerSettings settings) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
        document.addPage(page);

        // Coordinates = milimeters * PDPage.MM_TO_UNITS

        ByteArrayOutputStream os = QRCode
                .from("Hello World")
                .withSize(Float.valueOf(30 * MM_TO_UNITS).intValue(), Float.valueOf(30 * MM_TO_UNITS).intValue())
                .to(ImageType.JPG)
                .withCharset("utf-8")
                .stream();

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        PDJpeg img = new PDJpeg(document, is);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.drawImage(img, 30 * MM_TO_UNITS, 200 * MM_TO_UNITS);
        contentStream.close();

        return new Result(document);
    }
}
