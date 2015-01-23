import Ember from 'ember';

export default Ember.ObjectController.extend({
    needs: "application",
    prefixSorting: ['power10'],
    sortedPrefixes: Ember.computed.sort('controllers.application.availableIsoPrefixes', 'prefixSorting'),
    unitSorting: ['name'],
    sortedUnits: Ember.computed.sort('controllers.application.availableUnits', 'unitSorting')
});
