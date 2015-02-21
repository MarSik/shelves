package org.marsik.elshelves.api;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    public PageSize getPage() {
        return page;
    }

    public StickerSettings setPage(PageSize page) {
        this.page = page;
        return this;
    }

    public Float getLeftMarginMm() {
        return leftMarginMm;
    }

    public StickerSettings setLeftMarginMm(Float leftMarginMm) {
        this.leftMarginMm = leftMarginMm;
        return this;
    }

    public Float getTopMarginMm() {
        return topMarginMm;
    }

    public StickerSettings setTopMarginMm(Float topMarginMm) {
        this.topMarginMm = topMarginMm;
        return this;
    }

    public Float getStickerWidthMm() {
        return stickerWidthMm;
    }

    public StickerSettings setStickerWidthMm(Float stickerWidthMm) {
        this.stickerWidthMm = stickerWidthMm;
        return this;
    }

    public Float getStickerHeightMm() {
        return stickerHeightMm;
    }

    public StickerSettings setStickerHeightMm(Float stickerHeightMm) {
        this.stickerHeightMm = stickerHeightMm;
        return this;
    }

    public Float getStickerLeftMarginMm() {
        return stickerLeftMarginMm;
    }

    public StickerSettings setStickerLeftMarginMm(Float stickerLeftMarginMm) {
        this.stickerLeftMarginMm = stickerLeftMarginMm;
        return this;
    }

    public Float getStickerTopMarginMm() {
        return stickerTopMarginMm;
    }

    public StickerSettings setStickerTopMarginMm(Float stickerTopMarginMm) {
        this.stickerTopMarginMm = stickerTopMarginMm;
        return this;
    }

    public Float getBottomSpaceMm() {
        return bottomSpaceMm;
    }

    public StickerSettings setBottomSpaceMm(Float bottomSpaceMm) {
        this.bottomSpaceMm = bottomSpaceMm;
        return this;
    }

    public Float getRightSpaceMm() {
        return rightSpaceMm;
    }

    public StickerSettings setRightSpaceMm(Float rightSpaceMm) {
        this.rightSpaceMm = rightSpaceMm;
        return this;
    }

    public Integer getStickerHorizontalCount() {
        return stickerHorizontalCount;
    }

    public StickerSettings setStickerHorizontalCount(Integer stickerHorizontalCount) {
        this.stickerHorizontalCount = stickerHorizontalCount;
        return this;
    }

    public Integer getStickerVerticalCount() {
        return stickerVerticalCount;
    }

    public StickerSettings setStickerVerticalCount(Integer stickerVerticalCount) {
        this.stickerVerticalCount = stickerVerticalCount;
        return this;
    }

    public Boolean getPrintName() {
        return printName;
    }

    public StickerSettings setPrintName(Boolean printName) {
        this.printName = printName;
        return this;
    }
}
