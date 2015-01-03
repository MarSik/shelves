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

    typeSorting: ['name'],
    sortedTypes: Ember.computed.sort('types', 'typeSorting'),
    sourceSorting: ['name'],
    sortedSources: Ember.computed.sort('sources', 'sourceSorting')
});
