import Ember from 'ember';
import ENV from '../../config/environment';
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
            var newLot = this.store.createRecord('lot', {
                previous: lot,
                usedBy: req,
                count: count
            });

            var self = this;

            newLot.save().catch(function (e) {
                newLot.destroy();
                lot.rollback();
                self.growl.error(e);
            }).then(function (d) {
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
            var newLot = this.store.createRecord('lot', {
                previous: lot,
                action: "UNASSIGNED"
            });

            var self = this;

            newLot.save().catch(function (e) {
                newLot.destroy();
                lot.rollback();
                self.growl.error(e);
            });
        },
        solderLot: function (lot) {
            var newLot = this.store.createRecord('lot', {
                previous: lot,
                action: "SOLDERED"
            });

            var self = this;

            newLot.save().catch(function (e) {
                newLot.destroy();
                lot.rollback();
                self.growl.error(e);
            });
        },
        unsolderLot: function (lot) {
            var newLot = this.store.createRecord('lot', {
                previous: lot,
                action: "UNSOLDERED"
            });

            var self = this;

            newLot.save().catch(function (e) {
                newLot.destroy();
                lot.rollback();
                self.growl.error(e);
            });
        },
        importRequirements: function (document) {
            var url = ENV.APP.API_ENDPOINT + '/projects/' + this.get('model.id') + '/import';
            $.post(url, {
                document: document.get('id')
            }, function (data) {
                this.get('store').pushPayload('project', data);
            });
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
    importableDocuments: Ember.computed.filterBy('model.describedBy', 'contentType', 'application/x-kicad-schematic')
});
