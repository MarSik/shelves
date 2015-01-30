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
    prefixSorting: ['base', 'power'],
    sortedPrefixes: Ember.computed.sort('controllers.application.availableSiPrefixes', 'prefixSorting')
});
