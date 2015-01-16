package org.marsik.elshelves.backend.services;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.marsik.elshelves.backend.dtos.StickerSettings;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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

    private static final float MM_TO_UNITS = 72/(10*2.54f);

    private static enum Rotation {
        LANDSCAPE,
        PORTRAIT
    }

    private static enum PageMapping {
        A4(PDPage.PAGE_SIZE_A4),
        A4R(PDPage.PAGE_SIZE_A4, Rotation.LANDSCAPE),
        LETTER(PDPage.PAGE_SIZE_LETTER),
        LETTERR(PDPage.PAGE_SIZE_LETTER, Rotation.LANDSCAPE);

        final PDRectangle pageFormat;
        final Rotation rotation;

        PageMapping(PDRectangle pageFormat) {
            this.pageFormat = pageFormat;
            this.rotation = Rotation.PORTRAIT;
        }

        PageMapping(PDRectangle pageFormat, Rotation rotation) {
            this.pageFormat = pageFormat;
            this.rotation = rotation;
        }

        void updatePage(PDPage page) {
            if (rotation.equals(Rotation.LANDSCAPE)) {
                page.setRotation(90);
            }
        }

        void updateContentStream(PDPage page, PDPageContentStream contentStream) throws IOException {
            if (rotation.equals(Rotation.LANDSCAPE)) {
                // Transform coordinates so they still start in lower left corner
                final float pageWidth = page.getMediaBox().getWidth();
                contentStream.concatenate2CTM(0, 1, -1, 0, pageWidth, 0);
            }
        }
    }

    public Result generateStickers(StickerSettings settings, List<StickerCapable> objects) throws IOException {
        PDDocument document = new PDDocument();
        // Coordinates = millimeters * PDPage.MM_TO_UNITS

        PageMapping pageMapping = PageMapping.valueOf(settings.getPage().name());
        Float pageHeight = pageMapping.rotation.equals(Rotation.PORTRAIT) ?
                pageMapping.pageFormat.getHeight() : pageMapping.pageFormat.getWidth();

        int pageCount = 0;
        int stickerCount = 0;

        Float codeEdgeSize = MM_TO_UNITS * Math.min(settings.getStickerHeightMm() - 2*settings.getStickerTopMarginMm(),
                settings.getStickerWidthMm()) - 2*settings.getStickerLeftMarginMm();

        PDPageContentStream contentStream = null;

        for (StickerCapable object: objects) {
            stickerCount++;

            if (stickerCount > pageCount * settings.getStickerHorizontalCount() * settings.getStickerVerticalCount()) {
                if (contentStream != null) {
                    contentStream.close();
                }

                PDPage page = new PDPage(pageMapping.pageFormat);
                pageMapping.updatePage(page);
                document.addPage(page);
                pageCount++;

                contentStream = new PDPageContentStream(document, page);
                pageMapping.updateContentStream(page, contentStream);
            }

            PDJpeg img = getQRImage(document, object, codeEdgeSize);

            int noInPage = ((stickerCount - 1) % (settings.getStickerHorizontalCount() * settings.getStickerVerticalCount()));
            int row = noInPage / settings.getStickerHorizontalCount();
            int col = noInPage % settings.getStickerHorizontalCount();

            float leftCoord = (settings.getLeftMarginMm() * MM_TO_UNITS
                    + col * (settings.getStickerWidthMm() + settings.getRightSpaceMm()) * MM_TO_UNITS
                    + settings.getStickerLeftMarginMm() * MM_TO_UNITS);
            float topCoord = (settings.getTopMarginMm() * MM_TO_UNITS
                    + row * (settings.getStickerHeightMm() + settings.getBottomSpaceMm()) * MM_TO_UNITS
                    + settings.getStickerTopMarginMm() * MM_TO_UNITS);

            /*
              The y coordinate of the image has to be converted to lower-left coordinate system
              by adding the image height to the top-left coordinates.
             */
            contentStream.drawImage(img, leftCoord, pageHeight - (topCoord + codeEdgeSize));
			contentStream.beginText();
            contentStream.drawString(object.getName());
			contentStream.endText();

			contentStream.beginText();
			contentStream.drawString(object.getSummary());
			contentStream.endText();
        }

        if (contentStream != null) {
            contentStream.close();
        }

        return new Result(document);
    }

    private PDJpeg getQRImage(PDDocument document, StickerCapable object, Float size) throws IOException {
        ByteArrayOutputStream os = QRCode
                .from("shv://" + object.getBaseUrl() + "/" + object.getUuid())
                .withSize(size.intValue(), size.intValue())
                .to(ImageType.JPG)
                .withErrorCorrection(ErrorCorrectionLevel.M)
                .withCharset("utf-8")
                .stream();
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        PDJpeg img = new PDJpeg(document, is);
        return img;
    }
}
