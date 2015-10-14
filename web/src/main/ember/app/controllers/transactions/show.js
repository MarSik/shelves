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
    itemSorting: ['type.name'],
    sortedItems: Ember.computed.sort('model.items', 'itemSorting'),
    location: null,
    editEnabled: Ember.computed('model.locked', function () {
        return !this.get('model.locked');
    })
});
