import Ember from 'ember';
/* global $ */

export default Ember.Controller.extend({
    needs: "application",
    actions: {
        showAddAlternative: function (req) {
            this.set('lastRequirement', req);
            $("#addAlternative").foundation("reveal", "open");
        },
        addAlternativePart: function () {
            $("#addAlternative").foundation("reveal", "close");
            return true;
        },
        assignLot: function (req) {
            this.set('assignLotToRequirement', req);
            $("#addLot").foundation("reveal", "open");
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
                if (req.get('missing') <= 0) {
                    $("#addLot").foundation("reveal", "close");
                }
            });
        },
        closeAssignment: function () {
            $("#addLot").foundation("reveal", "close");
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
    candidateLots: function () {
        var req = this.get('assignLotToRequirement');

        console.log('Recomputing available parts');

        var usable = Ember.A();

        if (Ember.isEmpty(req)) {
            return usable;
        }

        req.get('type').forEach(function (type) {
            console.log('type:');
            console.log(type);
            type.get('lots').forEach(function (lot) {
                console.log(lot);
                usable.pushObject(lot);
            });
        });

        console.log(usable);

        return usable;
    }.property('assignLotToRequirement.type.@each.lots'),

    assignableLots: Ember.computed.filterBy('candidateLots', 'canBeAssigned', true)
});
