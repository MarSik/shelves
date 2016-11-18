import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        createEmptyFootprint: function (name) {
            var self = this;

            var g = this.store.createRecord('footprint', {
                name: name,
                flagged: true
            });

            g.save().catch(function () {
                g.destroy();
            }).then(function (fp) {
                self.get('model.footprints').pushObject(fp);
            });
        },
        createEmptyGroup: function (name) {
            var self = this;
            var g = this.store.createRecord('group', {
                name: name,
                flagged: true
            });

            g.save().catch(function () {
                g.destroy();
            }).then(function (g) {
                self.get('model.groups').pushObject(g);
            });
        }
    },
    application: Ember.inject.controller("application"),
    footprintSorting: ['name'],
    sortedFootprints: Ember.computed.sort('application.availableFootprints', 'footprintSorting'),
    groupSorting: ['fullName'],
    sortedGroups: Ember.computed.sort('application.availableGroups', 'groupSorting'),
    incompleteForm: function () {
        return Ember.isEmpty(this.get('model.name'));
    }.property('model.name')
});
