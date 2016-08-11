import Ember from 'ember';

export default Ember.Component.extend({
  growl: Ember.inject.service(),
  actions: {
    deleteDocument(doc) {
      if (doc.get("describes.length") == 1) this.sendAction("deleteDocument", doc);
      else this.get('growl').error("This document describes multiple entities. Please use the detail link to see them first.");
    }
  },
  tagName: 'tbody'
  // doc
});
