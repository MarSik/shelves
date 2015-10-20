import Ember from 'ember';

export default Ember.Component.extend({
  tagName: '',
  model: undefined,
  sorting: undefined,
  showProperties: null,
  columnCount: null,
  sortedTypes: Ember.computed.sort('model.types', 'sorting')
});
