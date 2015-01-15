import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        assign: function (lot, requirement) {
            var newLot = this.store.createRecord('lot', {
                previous: lot,
                usedBy: requirement,
                count: requirement.get('missing')
            });
            newLot.save().catch(function () {
                newLot.revert();
            });
        },
        unassign: function (lot) {
            var newLot = this.store.createRecord('lot', {
                previous: lot,
                action: "UNASSIGNED"
            });
            newLot.save().catch(function () {
                newLot.revert();
            });
        },
        solder: function (lot) {
            var newLot = this.store.createRecord('lot', {
                previous: lot,
                action: "SOLDERED"
            });
            newLot.save();
        },
        unsolder: function (lot) {
            var newLot = this.store.createRecord('lot', {
                previous: lot,
                action: "UNSOLDERED"
            });
            newLot.save().catch(function () {
                newLot.revert();
            });
        }
    }
});
