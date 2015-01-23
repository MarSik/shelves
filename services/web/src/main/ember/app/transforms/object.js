import DS from 'ember-data';
import Ember from 'ember';

export default DS.Transform.extend({
    serialize: function (value) {
        var propertyNames = value.get('propertyNames') || [];
        return Ember.isNone(value) ? {} : value.getProperties(propertyNames);
    },
    deserialize: function (value) {
        var obj = Ember.Object.create();
        if (!Ember.isNone(value)) {
            obj.setProperties(value);
        }
        return obj;
    }
});
