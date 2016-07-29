import DS from 'ember-data';
import IdentifiedBase from './identifiedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default IdentifiedBase.extend({
  version: attr(),
  name: attr('string'),
    summary: attr(),
    description: attr(),
    url: attr('string'),
    contentType: attr('string'),
    size: attr('number'),
    created: attr('date'),
    belongsTo: belongsTo('user', {async: true}),
    describes: hasMany('namedbase', {async: true, polymorphic: true}),

    link: function() {
        return "documents.show";
    }.property(),

    icon: function () {
        var ct = this.get('contentType');
        if (ct === "text/plain") {
            return "file-text-o";
        } else if (ct === "application/pdf") {
            return "file-pdf-o";
        } else if (ct === "image/jpeg") {
            return "file-image-o";
        } else if (ct === "image/png") {
            return "file-image-o";
        } else if (ct === "image/gif") {
            return "file-image-o";
        } else if (ct === "application/x-kicad-schematic") {
            return "file";
        } else {
            return "file-o";
        }
    }.property('contentType'),
    supportsAttachments: function() {
      return false;
    }.property()
});
