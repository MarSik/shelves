import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        addProperty: function (group, property) {
            group.get('showProperties').pushObject(property);
            var self = this;
            group.save().catch(function (e) {
                group.rollback();
                self.growl.error("Could not add property: "+e);
            });
        },
        removeProperty: function (group, property) {
            group.get('showProperties').removeObject(property);
            var self = this;
            group.save().catch(function (e) {
                group.rollback();
                self.growl.error("Could not remove property: "+e);
            });
        },
        sortBy: function (p, desc) {
            var key = p;
            if (desc) {
                key += ':desc';
            }
            this.set('typeSorting', [key]);
        },
        sortByProperty: function (property, desc) {
            var key = 'values.'+property.get('id');
            if (desc) {
                key += ':desc';
            }
            this.set('typeSorting', [key]);
        },
        manageProperties: function (value) {
            this.set('manageProperties', value);
        }
    },
    needs: "application",
    propSorting: ['name'],
    sortedProperties: Ember.computed.sort('controllers.application.availableProperties', 'propSorting'),
    typeColumns: function () {
        return 5 + this.get('model.showProperties.length');
    }.property('model.showProperties.size'),

    typeSorting: ['name'],
    sortedTypes: Ember.computed.sort('model.types', 'typeSorting')
});
