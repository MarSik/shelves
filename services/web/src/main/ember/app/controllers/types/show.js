import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        addProperty: function (entity, property, value, prefix) {
            var normalizedValue = value;

            if (Ember.isEmpty(prefix)) {
            } else {
                var prefixPower = prefix.get('power10');
                var basePower = property.get('base.power10');
                var multiplyPower = prefixPower - basePower;
                normalizedValue = value * Math.pow(10, multiplyPower);
            }

            var propertyId = property.get('id');
            entity.get('properties').pushObject(property);

            var values = entity.get('values');
            values.set(propertyId, normalizedValue);

            var self = this;
            entity.save().catch(function (e) {
                e.rollback();
            });
        },
        removeProperty: function (entity, property) {
            entity.get('properties').removeObject(property);
            entity.get('values').set(property.get('id'), null);

            entity.save().catch(function (e) {
                e.rollback();
            });
        },
        enableAddProperty: function () {
            this.set('propertyAddingAllowed', true);
        },
        disableAddProperty: function () {
            this.set('propertyAddingAllowed', false);
        }
    },
    needs: "application",
    propertySorting: ['name'],
    sortedProperties: Ember.computed.sort('controllers.application.availableProperties', 'propertySorting'),

    unitPrefixes: function () {
        return this.get('propertyToAdd.unit.prefixes');
    }.property('propertyToAdd.unit.prefixes'),

    prefixSorting: ['power10'],
    sortedUnitPrefixes: Ember.computed.sort('unitPrefixes', 'prefixSorting'),

    propertyAddingAllowed: false,
    addPropertyIncomplete: function () {
        return Ember.isEmpty(this.get('propertyToAdd')) || Ember.isEmpty(this.get('propertyValueToAdd'));
    }.property('propertyToAdd', 'propertyValueToAdd')
});
