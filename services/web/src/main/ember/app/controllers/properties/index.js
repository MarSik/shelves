import Ember from 'ember';
/* global $ */

export default Ember.ArrayController.extend({
    needs: "application",
    actions: {
        showCreateProperty: function () {
            $("#createProperty").foundation("reveal", "open");
        },
        createProperty: function (name, unit, base) {
            var newProperty = this.store.createRecord('property', {
                name: name,
                unit: unit,
                base: base
            });
            var self = this;
            $("#createProperty").foundation("reveal", "close");

            newProperty.save()
                .then(function() {
                    self.growl.info("Property created");
                })
                .catch(function(e) {
                    newProperty.rollback();
                    self.growl.error("Property creation failed: "+e);
                });
        }
    },
    sortProperties: ['name'],
    sortAscending: true,
    createDisabled: function() {
        return Ember.isEmpty(this.get('propertyName')) || Ember.isEmpty(this.get('propertyUnit')) || Ember.isEmpty(this.get('propertyBase'));
    }.property('propertyName', 'propertyUnit', 'propertyBase'),
    prefixSorting: ['power10'],
    sortedPrefixes: Ember.computed.sort('controllers.application.availableIsoPrefixes', 'prefixSorting'),
    unitSorting: ['name'],
    sortedUnits: Ember.computed.sort('controllers.application.availableUnits', 'unitSorting')
});