import Ember from 'ember';
import ENV from '../../../config/environment';
/* global $ */

export default Ember.Controller.extend({
    needs: "application",
    actions: {
        showAddAlternative: function (req) {
            this.set('lastRequirement', req);
            this.set('showAlternativeDialog', true);
        },
        addAlternativePart: function () {
            this.set('showAlternativeDialog', false);
            return true;
        },
        assignLot: function (req, lots) {
            this.set('assignLotToRequirement', req);
            this.set('assignableLots', lots);
        },
        performAssignment: function (req, lot, count) {
            nl = this.store.createRecord('lot', {
              previous: lot,
              status: "ASSIGNED",
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
                    self.set('assignableLots', []);
                }
            });
        },
        closeAssignment: function () {
            this.set('assignLotToRequirement', null);
            this.set('assignableLots', []);
        },
        unassignLot: function (lot) {
            lot.set('status', 'UNASSIGNED');
            lot.set('usedBy', null);

            var self = this;

            lot.save().catch(function (e) {
                lot.rollback();
                self.growl.error(e);
            });
        },
        solderLot: function (lot, count) {
          lot.set('status', 'SOLDERED');
          lot.set('count', count);

          console.log("Lot count ", arguments);

          var self = this;

          lot.save().catch(function (e) {
            lot.rollback();
            self.growl.error(e);
          });
        },
        unsolderLot: function (lot) {
          lot.set('status', 'UNSOLDERED');

          var self = this;

          lot.save().catch(function (e) {
            lot.rollback();
            self.growl.error(e);
          });
        },
        importRequirements: function (document) {
            var url = ENV.APP.API_ENDPOINT + '/projects/' + this.get('model.id') + '/import';
            var self = this;
            $.post(url, {
                document: document.get('id')
            }, function (data) {
                self.get('store').pushPayload('project', data);
            });
        },
        toggleImportRequirements: function () {
            this.set('showImportRequirements', !this.get('showImportRequirements'));
        },
        toggleAddRequirement: function () {
            this.set('showAddRequirement', !this.get('showAddRequirement'));
        }
    },
    typeSorting: ['name'],
    sortedTypes: Ember.computed.sort('controllers.application.availableTypes', 'typeSorting'),
    requiredType: null,
    requiredCount: 1,
    lastRequirement: null,
    alternativeType: null,
    submitDisabled: function() {
        return this.get('requiredType') == null || this.get('requiredCount') == null || this.get('requiredCount') < 1;
    }.property('requiredType', 'requiredCount'),

    assignLotToRequirement: null,
    assignableLots: [],

    displayRequirements: Ember.computed.map('model.requirements', m => m),
    importableDocuments: Ember.computed.filterBy('model.describedBy', 'contentType', 'application/x-kicad-schematic'),

    showAddRequirement: false,
    addRequirementClass: function () {
        if (this.get('showAddRequirement')) {
            return "primary button";
        } else {
            return "secondary button";
        }
    }.property('showAddRequirement'),

    showImportRequirements: false,
    importRequirementsClass: function () {
        if (this.get('showImportRequirements')) {
            return "primary button";
        } else {
            return "secondary button";
        }
    }.property('showImportRequirements'),

    importableDocumentPresent: function () {
        var docs = this.get('importableDocuments');
        return !Ember.isEmpty(docs);
    }.property('importableDocuments.@each')
});
