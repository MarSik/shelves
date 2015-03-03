import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        addProperty: function (entity, property, value, prefix) {
            var normalizedValue = value;

            if (Ember.isEmpty(prefix)) {
            } else {
                var prefixBase = prefix.get('base');
                var prefixPower = prefix.get('power');
                var baseBase = property.get('base.base');
                var basePower = property.get('base.power');
                var multiplyPower = Math.pow(prefixBase, prefixPower) / Math.pow(baseBase, basePower);
                normalizedValue = value * multiplyPower;
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
                self.get('model').save().catch(function (e) {
                    self.get('model.groups').removeObject(g);
                });
            });
        },
        removeGroup: function (group) {
            var model = this.model;
            model.get('groups').removeObject(group);
            model.save().catch(function () {
                model.rollback();
            });
        },
        showAddGroup: function () {
            this.set('displayAddGroup', true);
        },
        hideAddGroup: function () {
            this.set('displayAddGroup', false);
        },
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
                self.get('model').save().catch(function (e) {
                    self.get('model.footprints').removeObject(fp);
                });
            });
        },
        removeFootprint: function (footprint) {
            var model = this.model;
            model.get('footprints').removeObject(footprint);
            model.save().catch(function () {
                model.rollback();
            });
        },
        showAddFootprint: function () {
            this.set('displayAddFootprint', true);
        },
        hideAddFootprint: function () {
            this.set('displayAddFootprint', false);
        },
        addSeeAlso: function (type) {
            var model = this.model;
            if (type == this.model) {
                return;
            }

            model.get('seeAlso').pushObject(type);
            model.save().catch(function () {
                model.rollback();
            });
        },
        removeSeeAlso: function (type) {
            var model = this.model;
            model.get('seeAlso').removeObject(type);
            model.save().catch(function () {
                model.rollback();
            });
        },
        showAddSeeAlso: function () {
            this.set('displayAddSeeAlso', true);
        },
        hideAddSeeAlso: function () {
            this.set('displayAddSeeAlso', false);
        },
        createBox: function (name) {
            var box = this.store.createRecord('box', {
                name: name,
                flagged: true
            });

            box.save().catch(function (err) {
                this.growl.error(err);
                box.destroy();
            });
        },
        showMoveLot: function (lot) {
            this.set('moveLot', lot);
            this.set('moveLotCount', lot.get('count'));
            this.set('moveLotToBox', lot.get('location'));
        },
        moveLot: function (lot, destination, count) {
            // close dialog
            this.set('moveLot', null);
            // bubble up
            return true;
        }
    },
    needs: "application",
    propertySorting: ['name'],
    sortedProperties: Ember.computed.sort('controllers.application.availableProperties', 'propertySorting'),

    typeSorting: ['fullName'],
    sortedTypes: Ember.computed.sort('controllers.application.availableTypes', 'typeSorting'),

    groupSorting: ['fullName'],
    sortedGroups: Ember.computed.sort('controllers.application.availableGroups', 'groupSorting'),

    footprintSorting: ['fullName'],
    sortedFootprints: Ember.computed.sort('controllers.application.availableFootprints', 'footprintSorting'),

    boxSorting: ['fullName'],
    sortedBoxes: Ember.computed.sort('controllers.application.availableLocations', 'boxSorting'),

    unitPrefixes: function () {
        return this.get('propertyToAdd.unit.prefixes');
    }.property('propertyToAdd.unit.prefixes', 'propertyToAdd.unit.prefixes.@each'),

    prefixSorting: ['power10'],
    sortedUnitPrefixes: Ember.computed.sort('unitPrefixes', 'prefixSorting'),

    propertyAddingAllowed: false,
    addPropertyIncomplete: function () {
        return Ember.isEmpty(this.get('propertyToAdd')) || Ember.isEmpty(this.get('propertyValueToAdd'));
    }.property('propertyToAdd', 'propertyValueToAdd'),

    moveLotToBox: null,
    moveLotDisabled: function () {
        return Ember.isEmpty(this.get('moveLotToBox')) || Ember.isEmpty(this.get('moveLotCount'));
    }.property('moveLotToBox', 'moveLotCount')
});
