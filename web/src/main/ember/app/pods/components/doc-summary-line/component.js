import Ember from 'ember';

export default Ember.Component.extend({
  growl: Ember.inject.service(),
  actions: {
    deleteDocument(doc) {
      if (doc.get("describes.length") == 1) this.get("deleteDocument")(doc);
      else this.get('growl').error("This document describes multiple entities. Please use the detail link to see them first.");
    }
  },
  tagName: 'tbody',
  size: Ember.computed('doc.size', function () {
    var val = this.get('doc.size');
    var suffix = 'B';

    if (val > 1024) {
      val = val / 1024.0;
      suffix = 'kiB';
    }

    if (val > 1024) {
      val = val / 1024.0;
      suffix = 'MiB';
    }

    val = Math.round(val * 1000) / 1000.0;

    return val + " " + suffix;
  })
  // doc
});
