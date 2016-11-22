import Ember from 'ember';
// global Promise

export default Ember.Component.extend({
  tagName: "tbody",

  actions: {
    solderLot(lot, count) {
      this.sendAction("solderLot", lot, count);
    },
    unsolderLot(lot, count) {
      this.sendAction("unsolderLot", lot, count);
    },
    unassignLot(lot, count) {
      this.sendAction("unassignLot", lot, count);
    },
    assignLot() {
      console.log("Assignable lot groups: ", JSON.stringify(this.get('assignableLotGroups')));
      this.sendAction("assignLot", this.get("r"));
    },
    showAddAlternative() {
      this.sendAction("showAddAlternative", this.get("r"));
    },
    removeRequirement() {
      this.sendAction("removeRequirement", this.get("r"));
    },
    removeAlternativePart(type) {
      this.sendAction("removeAlternativePart", this.get("r"), type);
    }
  },

  assignedLots: Ember.computed.filterBy('r.lots', 'valid', true),
  // r
});
