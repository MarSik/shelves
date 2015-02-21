package org.marsik.elshelves.backend.services;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.marsik.elshelves.api.StickerSettings;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
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
            } catch (IOException ex) {
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
        A4(PDRectangle.A4),
        A4R(PDRectangle.A4, Rotation.LANDSCAPE),
        LETTER(PDRectangle.LETTER),
        LETTERR(PDRectangle.LETTER, Rotation.LANDSCAPE);

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

    private float getFontHeight(PDFont font, int fontSize) {
        return (font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000) * fontSize;
    }

	private String getSingleLine(String text, float maxWidth, PDFont font, int fontSize, boolean nice) {
		int maxTrim = text.length();
		int minTrim = 0;
		String result = text;

		while (maxTrim >= minTrim) {
			int trim = (minTrim + maxTrim) / 2;
			String candidate = text.substring(0, trim);

			try {
				if (fontSize * (font.getStringWidth(candidate.trim()) / 1000) > maxWidth) {
					// Too long, but already close to the boundary
					// try increasing it (avoids rounding errors)
					if (maxTrim <= trim) {
						maxTrim--;
						continue;
					}

					// Too long, try smaller next time
					maxTrim = trim;
				} else {
					// Store the best result so far
					result = candidate;

					// Short enough, but already close to the boundary
					// try increasing it (avoids rounding errors)
					if (minTrim >= trim) {
						minTrim++;
						continue;
					}

					// Short enough, try larger next time
					minTrim = trim;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				return result;
			}
		}

		return result;
	}

    public Result generateStickers(StickerSettings settings, List<StickerCapable> objects) throws IOException {
        PDDocument document = new PDDocument();
        // Coordinates = millimeters * PDPage.MM_TO_UNITS

        PageMapping pageMapping = PageMapping.valueOf(settings.getPage().name());
        Float pageHeight = pageMapping.rotation.equals(Rotation.PORTRAIT) ?
                pageMapping.pageFormat.getHeight() : pageMapping.pageFormat.getWidth();

        int pageCount = 0;
        int stickerCount = 0;

        // Load unicode fonts
        PDFont titleFont = PDType0Font.load(document, this.getClass().getResourceAsStream("/org/marsik/elshelves/backend/fonts/DejaVuSans-Bold.ttf"));
        PDFont summaryFont = PDType0Font.load(document, this.getClass().getResourceAsStream("/org/marsik/elshelves/backend/fonts/DejaVuSans.ttf"));


        Float codeEdgeSize = MM_TO_UNITS * Math.min(settings.getStickerHeightMm() - 2*settings.getStickerTopMarginMm(),
                settings.getStickerWidthMm()) - 2*settings.getStickerLeftMarginMm() - getFontHeight(titleFont, settings.getTitleFontSize());

		Float titleWidth = MM_TO_UNITS * (settings.getStickerWidthMm() - 2*settings.getStickerLeftMarginMm());
		Float summaryWidth = (MM_TO_UNITS * (settings.getStickerWidthMm() - 2*settings.getStickerLeftMarginMm())) - codeEdgeSize;
		Float summaryHeight = (MM_TO_UNITS * (settings.getStickerHeightMm() - 2*settings.getStickerTopMarginMm())) - getFontHeight(titleFont, settings.getTitleFontSize());

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

            PDImageXObject img = getQRImage(document, object, codeEdgeSize);

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
            contentStream.drawImage(img, leftCoord, pageHeight - (topCoord + getFontHeight(titleFont, settings.getTitleFontSize()) + codeEdgeSize));

            if (object.getName() != null) {
                contentStream.beginText();
                contentStream.setFont(titleFont, settings.getTitleFontSize());
                contentStream.newLineAtOffset(leftCoord, pageHeight - (topCoord + getFontHeight(titleFont, settings.getTitleFontSize())));
                contentStream.showText(getSingleLine(object.getName(), titleWidth, titleFont, settings.getTitleFontSize(), false));
                contentStream.endText();
            }

            if (object.getSummary() != null) {
                contentStream.beginText();
                contentStream.setFont(summaryFont, settings.getDetailsFontSize());
				contentStream.setLeading(getFontHeight(summaryFont, settings.getDetailsFontSize()));
                contentStream.newLineAtOffset(leftCoord + codeEdgeSize, pageHeight - (topCoord
                        + getFontHeight(titleFont, settings.getTitleFontSize()) + getFontHeight(summaryFont, settings.getDetailsFontSize())));

				int idx = 0;
				int lines = (int)Math.floor(summaryHeight / (getFontHeight(summaryFont, settings.getDetailsFontSize())));

				while (lines > 0 && idx < object.getSummary().length()) {
					lines--;
					String part = getSingleLine(object.getSummary().substring(idx),
							summaryWidth,
							summaryFont,
							settings.getDetailsFontSize(),
							true);
					contentStream.showText(part.trim());
					if (lines > 0) {
						contentStream.newLine();
					}
					idx += part.length();
				}

                contentStream.endText();
            }
        }

        if (contentStream != null) {
            contentStream.close();
        }

        return new Result(document);
    }

    protected static PDImageXObject getQRImage(PDDocument document, StickerCapable object, Float size) throws IOException {
        ByteArrayOutputStream os = QRCode
                .from("shv://" + object.getBaseUrl() + "/" + object.getUuid())
                .withSize(size.intValue(), size.intValue())
                .to(ImageType.PNG)
                .withErrorCorrection(ErrorCorrectionLevel.M)
                .withCharset("utf-8")
                .withHint(EncodeHintType.MARGIN, 2) // violates the QR standard (should be 4)
                .stream();
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        PDImageXObject img = LosslessFactory.createFromImage(document, ImageIO.read(is));
        return img;
    }
}
