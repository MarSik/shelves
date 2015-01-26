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
        assignLot: function (req, type) {
            this.set('assignLotFromType', type);
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
    assignLotFromType: null,
    assignableLots: function () {
        var type = this.get('assignLotFromType');
        if (Ember.isEmpty(type)) {
            return [];
        }

        var usable = [];

        type.get('lots').forEach(function (lot) {
            if (lot.get('canBeAssigned')) {
                usable.pushObject(lot);
            }
        });

        return usable;
    }.property('assignLotFromType', 'assignLotFromType.lots.@each.canBeAssigned')
});
