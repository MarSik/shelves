import Ember from 'ember';

export default Ember.Component.extend({
  tagName: "li",
  classNames: ["item-tree"],
  sorting: ['fullName'],
  sorted: Ember.computed.sort('model.children', 'sorting'),

  model: undefined,
  target: undefined
});
