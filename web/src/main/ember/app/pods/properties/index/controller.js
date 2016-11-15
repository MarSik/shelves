import Ember from 'ember';
/* global $ */

export default Ember.Controller.extend({
    needs: "application",
    actions: {
        showCreateProperty: function () {
          this.set('showCreateDialog', true);
        },
        createProperty: function (name, unit, base) {
            var newProperty = this.store.createRecord('property', {
                name: name,
                unit: unit,
                base: base
            });
            var self = this;
          this.set('showCreateDialog', false);

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
    prefixSorting: ['base', 'power'],
    sortedPrefixes: Ember.computed.sort('controllers.application.availableSiPrefixes', 'prefixSorting'),
    unitSorting: ['name'],
    sortedUnits: Ember.computed.sort('controllers.application.availableUnits', 'unitSorting'),
    propertyNotComplete: function () {
        return Ember.isEmpty(this.get('propertyName')) || Ember.isEmpty(this.get('propertyBase')) || Ember.isEmpty(this.get('propertyUnit'));
    }.property('propertyName', 'propertyBase', 'propertyUnit')
});
