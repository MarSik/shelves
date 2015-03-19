import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        sortBy: function (p, desc) {
            var key = p;
            if (desc) {
                key += ':desc';
            }
            this.set('lotSorting', [key]);
        }
    },
    lots: Ember.computed.filterBy('model.lots', 'valid', true),
    // Show everything that is not soldered
    availableLots: Ember.computed.filterBy('lots', 'canBeUnsoldered', false),
    lotSorting: ['type.name'],
    sortedLots: Ember.computed.sort('availableLots', 'lotSorting')
});
