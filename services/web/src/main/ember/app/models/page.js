import DS from 'ember-data';

var attr = DS.attr;

export default DS.Model.extend({
    name: attr('string'),
    custom: attr('boolean'),

    pageSize: attr('string'),

    topMarginMm: attr('number'),
    leftMarginMm: attr('number'),

    stickerHorizontalCount: attr('number'),
    stickerVerticalCount: attr('number'),

    stickerWidthMm: attr('number'),
    stickerHeightMm: attr('number'),

    stickerTopMarginMm: attr('number'),
    stickerLeftMarginMm: attr('number'),

    rightSpaceMm: attr('number'),
    bottomSpaceMm: attr('number'),

    titleFontSize: attr('number'),
    detailsFontSize: attr('number')
});
