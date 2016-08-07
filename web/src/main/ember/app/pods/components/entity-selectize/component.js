import Ember from 'ember';

export default Ember.Component.extend({
  tagName: "",
  actions: {
    show() {
      this.set('display', true);
    },
    hide() {
      this.set('display', false);
    }
  },
  display: false,
  options: [],
  sorting: ['fullName'],
  sortedOptions: Ember.computed.sort('options', 'sorting')
});
