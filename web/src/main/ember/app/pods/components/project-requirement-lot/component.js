import Ember from 'ember';

export default Ember.Component.extend({
  tagName: "tr",

  actions: {
    showActions: function () {
      this.set('displayActions', true);
    },
    hideActions: function () {
      this.set('displayActions', false);
    },
    solderLot: function (lot, count) {
      this.set('displayActions', false);
      this.sendAction("solderLot", lot, count);
    },
    unsolderLot: function (lot, count) {
      this.set('displayActions', false);
      this.sendAction("unsolderLot", lot, count);
    },
    unassignLot: function (lot, count) {
      this.set('displayActions', false);
      this.sendAction("unassignLot", lot, count);
    }
  },

  displayActions: false

  // lot
});
