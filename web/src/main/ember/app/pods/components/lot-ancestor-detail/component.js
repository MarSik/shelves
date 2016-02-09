import Ember from 'ember';

export default Ember.Component.extend({
  tagName: "",
  ancestor: undefined, // base ancestor object
  showHistory: false,

  actions: {
    loadHistory() {
      this.set("showHistory", true);
    }
  }
});
