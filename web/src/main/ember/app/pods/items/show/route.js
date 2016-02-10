import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        assign: function (lot, requirement) {
          lot.set('usedBy', requirement);
          lot.set('count', requirement.get('missing'));
          lot.save().catch(function () {
            lot.revert();
          });
        },
        unassign: function (lot) {
          lot.set('usedBy', null);
          lot.save().catch(function () {
            lot.revert();
          });
        },
        solder: function (lot) {
          lot.set('used', true);
          lot.save().catch(function () {
            lot.revert();
          });
        },
        unsolder: function (lot) {
            lot.set('used', false);
            lot.save().catch(function () {
                lot.revert();
            });
        },
      finish: function (lot) {
        lot.set('finished', true);
        lot.save().catch(function () {
          lot.revert();
        });
      },
      reopen: function (lot) {
        lot.set('finished', false);
        lot.save().catch(function () {
          lot.revert();
        });
      }
    },

    afterModel() {
      this.store.findAll('type');
    }
});
