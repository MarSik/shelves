import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        addRow: function () {
            this.store.createRecord('purchase', {
                vat: 20,
                vatIncluded: true,
                transaction: this.get('model')
            });
        }
    },

    selectedSource: null,
    boxSorting: ['fullName'],
    sortedBoxes: Ember.computed.sort('boxes', 'boxSorting'),
    typeSorting: ['name'],
    sortedTypes: Ember.computed.sort('types', 'typeSorting'),
    sourceSorting: ['name'],
    sortedSources: Ember.computed.sort('sources', 'sourceSorting')
});
