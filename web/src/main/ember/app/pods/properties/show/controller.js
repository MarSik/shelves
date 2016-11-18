import Ember from 'ember';

export default Ember.Controller.extend({
    application: Ember.inject.controller("application"),
    prefixSorting: ['base', 'power'],
    sortedPrefixes: Ember.computed.sort('application.availableSiPrefixes', 'prefixSorting'),
    unitSorting: ['name'],
    sortedUnits: Ember.computed.sort('application.availableUnits', 'unitSorting')
});
