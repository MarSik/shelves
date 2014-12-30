import Ember from 'ember';

export default Ember.Controller.extend({
    footprintSorting: ['name'],
    sortedFootprints: Ember.computed.sort('availableFootprints', 'footprintSorting'),
    groupSorting: ['fullName'],
    sortedGroups: Ember.computed.sort('availableGroups', 'groupSorting')
});
