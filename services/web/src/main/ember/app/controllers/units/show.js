import Ember from 'ember';

export default Ember.ObjectController.extend({
    actions: {
        enablePrefix: function (prefix) {
            this.get('model.prefixes').pushObject(prefix);
        },
        disablePrefix: function (prefix) {
            this.get('model.prefixes').removeObject(prefix);
        }
    },
    needs: "application",
    prefixSorting: ['power10'],
    sortedPrefixes: Ember.computed.sort('controllers.application.availableIsoPrefixes', 'prefixSorting')
});
