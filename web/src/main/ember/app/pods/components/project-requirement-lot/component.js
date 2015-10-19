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
    solderLot: function (lot) {
      this.set('displayActions', false);
      this.sendAction("solderLot", lot);
    },
    unsolderLot: function (lot) {
      this.set('displayActions', false);
      this.sendAction("unsolderLot", lot);
    },
    unassignLot: function (lot) {
      this.set('displayActions', false);
      this.sendAction("unassignLot", lot);
    }
  },

  displayActions: false

  // lot
});
