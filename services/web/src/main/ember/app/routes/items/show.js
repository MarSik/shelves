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
          lot.set('action', 'UNASSIGNED');
          lot.save().catch(function () {
            lot.revert();
          });
        },
        solder: function (lot) {
          lot.set('action', 'SOLDERED');
          lot.save().catch(function () {
            lot.revert();
          });
        },
        unsolder: function (lot) {
            lot.set('action', 'UNSOLDERED');
            lot.save().catch(function () {
                lot.revert();
            });
        }
    }
});
