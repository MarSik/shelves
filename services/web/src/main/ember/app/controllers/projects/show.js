import Ember from 'ember';

export default Ember.Controller.extend({
    typeSorting: ['name'],
    sortedTypes: Ember.computed.sort('types', 'typeSorting'),
    requiredType: null,
    requiredCount: 1
});
