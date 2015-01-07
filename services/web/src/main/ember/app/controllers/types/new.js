import Ember from 'ember';

export default Ember.Controller.extend({
    needs: "application",
    footprintSorting: ['name'],
    sortedFootprints: Ember.computed.sort('controllers.application.availableFootprints', 'footprintSorting'),
    groupSorting: ['fullName'],
    sortedGroups: Ember.computed.sort('controllers.application.availableGroups', 'groupSorting')
});
