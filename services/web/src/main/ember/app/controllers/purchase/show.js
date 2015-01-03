import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        delivered: function () {
            console.log("delivering ..");
            return true;
        }
    },
    boxSorting: ['fullName'],
    sortedBoxes: Ember.computed.sort('boxes', 'boxSorting'),
    location: null
});
