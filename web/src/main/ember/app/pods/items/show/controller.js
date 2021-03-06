import Ember from 'ember';

export default Ember.Controller.extend({
    application: Ember.inject.controller("application"),
    actions: {
        showAddAlternative: function (req) {
            this.set('lastRequirement', req);
            this.set('showAlternativeDialog', true);
        },
        addAlternativePart: function () {
            this.set('showAlternativeDialog', false);
            return true;
        },
        assignLot: function (req, lotGroups) {
            this.set('assignLotToRequirement', req);
            this.set('assignableLotGroups', lotGroups);
        },
        performAssignment: function (req, lot, count) {
            var nl = this.store.createRecord('lot', {
              previous: lot,
              usedBy: req,
              count: count
            });

            var self = this;

            nl.save().catch(function (e) {
                nl.rollbackAttributes();
                self.growl.error(e);
            }).then(function () {
                console.log("MISSING");
                console.log(req.get('missing'));
                if (req.get('missing') <= 0) {
                    self.set('assignLotToRequirement', null);
                    self.set('assignableLotGroups', []);
                }
            });
        },
        closeAssignment: function () {
            this.set('assignLotToRequirement', null);
            this.set('assignableLotGroups', []);
        },
        unassignLot: function (lot) {
            lot.set('usedBy', null);

            var self = this;

            lot.save().catch(function (e) {
                lot.rollbackAttributes();
                self.growl.error(e);
            });
        },
        solderLot: function (lot, count) {
          lot.set('used', true);
          lot.set('count', count);

          console.log("Lot count ", arguments);

          var self = this;

          lot.save().catch(function (e) {
            lot.rollbackAttributes();
            self.growl.error(e);
          });
        },
        unsolderLot: function (lot) {
          lot.set('used', false);

          var self = this;

          lot.save().catch(function (e) {
            lot.rollbackAttributes();
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
    sortedTypes: Ember.computed.sort('application.availableTypes', 'typeSorting'),
    requiredType: null,
    requiredCount: 1,
    lastRequirement: null,
    alternativeType: null,
    submitDisabled: function() {
        return this.get('requiredType') == null || this.get('requiredCount') == null || this.get('requiredCount') < 1;
    }.property('requiredType', 'requiredCount'),

    assignLotToRequirement: null,
    assignableLotGroups: [],

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
