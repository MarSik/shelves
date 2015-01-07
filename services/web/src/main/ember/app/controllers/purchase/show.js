import Ember from 'ember';

export default Ember.Controller.extend({
    needs: "application",
    actions: {
        delivered: function () {
            console.log("delivering ..");
            return true;
        }
    },
    boxSorting: ['fullName'],
    sortedBoxes: Ember.computed.sort('controllers.application.availableLocations', 'boxSorting'),
    location: null
});
