import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        addProperty: function (group, property) {
            group.get('showProperties').pushObject(property);
            var self = this;
            group.save().catch(function (e) {
                group.rollback();
                self.growl.error("Could not add property: "+e);
            })
        },
        removeProperty: function (group, property) {
            group.get('showProperties').removeObject(property);
            var self = this;
            group.save().catch(function (e) {
                group.rollback();
                self.growl.error("Could not remove property: "+e);
            })
        }
    },
    needs: "application",
    propSorting: ['name'],
    sortedProperties: Ember.computed.sort('controllers.application.availableProperties', 'propSorting')
});
