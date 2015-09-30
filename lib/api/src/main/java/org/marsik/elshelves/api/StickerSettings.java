package org.marsik.elshelves.api;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class StickerSettings {
    public static enum PageSize {
        A4,
        A4R,
        LETTER,
        LETTERR
    }

    /**
     * Page size format
     */
    @NotNull
    PageSize page = PageSize.A4;

    /**
     * Font size for the first row
     */
    @NotNull
    @Min(6)
    @Max(64)
    Integer titleFontSize = 12;

    /**
     * Font size for the detail section
     */
    @NotNull
    @Min(4)
    @Max(64)
    Integer detailsFontSize = 10;

    /**
     * Space between left edge of the page and left edge of the first sticker
     */
    @NotNull
    @Min(0)
    Float leftMarginMm = 0f;

    /**
     * Space between top edge of the page and top edge of the first sticker
     */
    @NotNull
    @Min(0)
    Float topMarginMm = 0f;

    /**
     * Width of single sticker
     */
    @NotNull
    @Min(15)
    @Max(150)
    Float stickerWidthMm = 0f;

    /**
     * Height of single sticker
     */
    @NotNull
    @Min(15)
    @Max(150)
    Float stickerHeightMm = 0f;

    /**
     * Margin between left edge of the sticker and left edge of the content
     */
    @NotNull
    @Min(0)
    Float stickerLeftMarginMm = 0f;

    /**
     * Margin between top edge of the sticker and top edge of the content
     */
    @NotNull
    @Min(0)
    Float stickerTopMarginMm = 0f;

    /**
     * Vertical spacing between two stickers
     */
    @NotNull
    @Min(0)
    Float bottomSpaceMm = 0f;

    /**
     * Horizontal spacing between two stickers
     */
    @NotNull
    @Min(0)
    Float rightSpaceMm = 0f;

    /**
     * Number of stickers in a page row
     */
    @NotNull
    @Min(1)
    @Max(50)
    Integer stickerHorizontalCount = 1;

    /**
     * Number of stickers in a page column
     */
    @NotNull
    @Min(1)
    @Max(50)
    Integer stickerVerticalCount = 1;

    /**
     * Print name on the sticker
     */
    @NotNull
    Boolean printName = true;
}
