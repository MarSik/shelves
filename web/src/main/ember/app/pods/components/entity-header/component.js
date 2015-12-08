import Ember from 'ember';

export default Ember.Component.extend({
  actions: {
    deleteDocument(doc) {
      this.sendAction("deleteDocument", doc);
    }
  }
  // model
});
