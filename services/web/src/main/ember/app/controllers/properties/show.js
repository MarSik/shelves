import Ember from 'ember';

export default Ember.ObjectController.extend({
    needs: "application",
    prefixSorting: ['base', 'power'],
    sortedPrefixes: Ember.computed.sort('controllers.application.availableSiPrefixes', 'prefixSorting'),
    unitSorting: ['name'],
    sortedUnits: Ember.computed.sort('controllers.application.availableUnits', 'unitSorting')
});
