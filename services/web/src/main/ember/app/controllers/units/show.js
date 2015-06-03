import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        enablePrefix: function (prefix) {
            var unit = this.get('model');
            var prefixes = unit.get('prefixes');
            prefixes.pushObject(prefix);
            unit.save().catch(function () {
                unit.rollback();
            });
        },
        disablePrefix: function (prefix) {
            var unit = this.get('model');
            var prefixes = unit.get('prefixes');
            prefixes.removeObject(prefix);
            unit.save().catch(function () {
                unit.rollback();
            });
        }
    },
    needs: "application",
    prefixSorting: ['base', 'power'],
    sortedPrefixes: Ember.computed.sort('controllers.application.availableSiPrefixes', 'prefixSorting')
});
