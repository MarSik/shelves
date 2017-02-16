import Ember from 'ember';

export default Ember.Component.extend({
  tagName: "",
  ancestorRecord: undefined, // lot id
  ancestor: Ember.computed('ancestorRecord.lot', function () {
    return this.store.findRecord('lot', this.get('ancestorRecord.lot'));
  }),
  showHistory: false,
  probability: Ember.computed('ancestorRecord.count', 'ancestorRecord.outOf', function() {
    return 100.0 * this.get('ancestorRecord.count') / this.get('ancestorRecord.outOf');
  }),

  actions: {
    loadHistory() {
      this.set("showHistory", true);
    }
  }
});
