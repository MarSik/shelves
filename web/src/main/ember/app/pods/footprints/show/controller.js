import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        addSeeAlso: function (fp) {
            var model = this.model;
            if (fp === this.model) {
                return;
            }

            model.get('seeAlso').pushObject(fp);
            model.save().catch(function () {
                model.rollbackAttributes();
            });
        },
        removeSeeAlso: function (fp) {
            var model = this.model;
            model.get('seeAlso').removeObject(fp);
            model.save().catch(function () {
                model.rollbackAttributes();
            });
        },
        showAddSeeAlso: function () {
            this.set('displayAddSeeAlso', true);
        },
        hideAddSeeAlso: function () {
            this.set('displayAddSeeAlso', false);
        }
    },

    application: Ember.inject.controller("application"),

    fpSorting: ['name'],

    sortedFootprints: Ember.computed.sort('application.availableFootprints', 'fpSorting'),
    footprintTypes: Ember.computed.alias('application.availableFootprintTypes')
});
