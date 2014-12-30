import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        typeSelected: function (type) {
            this.set('selectedType', type.get('id'));
        },
        sourceSelected: function (source) {
            this.set('selectedSource', source.get('id'));
        },
        boxSelected: function (box) {
            this.set('selectedBox', box.get('id'));
        },
        addRow: function () {
            this.store.createRecord('purchase', {
                vat: 20,
                vatIncluded: true,
                transaction: this.get('model')
            });
        },
        purchase: function () {
            this.model.save().then(function (transaction) {

            }).catch(function (reason) {

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
