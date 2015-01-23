import Ember from 'ember';

export default Ember.ObjectController.extend({
    actions: {
        enablePrefix: function (prefix) {
            var unit = this.get('model');
            var prefixes = unit.get('prefixes');
            prefixes.pushObject(prefix);
            unit.save().catch(function (e) {
                unit.rollback();
            });
        },
        disablePrefix: function (prefix) {
            var unit = this.get('model');
            var prefixes = unit.get('prefixes');
            prefixes.removeObject(prefix);
            unit.save().catch(function (e) {
                unit.rollback();
            });
        }
    },
    needs: "application",
    prefixSorting: ['power10'],
    sortedPrefixes: Ember.computed.sort('controllers.application.availableIsoPrefixes', 'prefixSorting')
});
