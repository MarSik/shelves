import Ember from 'ember';
import ENV from 'webapp/config/environment';
/* global $ */

export default Ember.Controller.extend({
    application: Ember.inject.controller(),
    actions: {
        showAddAlternative: function (req) {
            this.set('lastRequirement', req);
            this.set('showAlternativeDialog', true);
        },
        addAlternativePart: function () {
            this.set('showAlternativeDialog', false);
            return true;
        },
        assignLot: function (req) {
            this.set('assignLotToRequirement', req);
        },
        performAssignment: function (req, lot, count) {
            var nl = this.store.createRecord('lot', {
              previous: lot,
              usedBy: req,
              count: count
            });

            var self = this;

            nl.save().catch(function (e) {
                nl.rollback();
                self.growl.error(e);
            }).then(function () {
                console.log("MISSING");
                console.log(req.get('missing'));
                if (req.get('missing') <= 0) {
                    self.set('assignLotToRequirement', null);
                }
            });
        },
        closeAssignment: function () {
            this.set('assignLotToRequirement', null);
        },
        unassignLot: function (lot) {
            lot.set('usedBy', null);

            var self = this;

            lot.save().catch(function (e) {
                lot.rollback();
                self.growl.error(e);
            });
        },
        solderLot: function (lot, count) {
          lot.set('used', true);
          lot.set('count', count);

          console.log("Lot count ", arguments);

          var self = this;

          lot.save().catch(function (e) {
            lot.rollback();
            self.growl.error(e);
          });
        },
        unsolderLot: function (lot) {
          lot.set('used', false);

          var self = this;

          lot.save().catch(function (e) {
            lot.rollback();
            self.growl.error(e);
          });
        }
    },

      typeSorting: ['name'],
      sortedTypes: Ember.computed.sort('application.availableTypes', 'typeSorting'),
    lastRequirement: null,
    alternativeType: null,

    assignLotToRequirement: null,

    displayRequirements: Ember.computed.map('model.requirements', m => m)
});
