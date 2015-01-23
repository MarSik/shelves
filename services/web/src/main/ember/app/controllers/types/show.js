import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        addProperty: function (entity, property, value) {
            entity.get('properties').pushObject(property);
            entity.get('values').set(property.get('id', value));
            var self = this;
            entity.save().catch(function (e) {
                console.log(e);
            });
        },
        removeProperty: function (entity, property) {
            entity.get('properties').removeObject(property);
        }
    }
});
