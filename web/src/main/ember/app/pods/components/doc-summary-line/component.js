import Ember from 'ember';

export default Ember.Component.extend({
  actions: {
    deleteDocument(doc) {
      this.sendAction("deleteDocument", doc);
    }
  },
  icon: function () {
    var ct = this.get('doc.contentType');
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
  }.property('doc.contentType'),
  tagName: 'tbody'
  // doc
});
