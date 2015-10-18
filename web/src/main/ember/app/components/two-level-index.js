import Ember from 'ember';

export default Ember.Component.extend({
  actions: {
    add(item) {
      this.get('createAction')(item);
    }
  },
  tagName: "li",
  sorting: ['fullName'],
  sorted: Ember.computed.sort('model.children', 'sorting'),

  createAction: undefined,
  model: undefined,
  target: undefined
});
