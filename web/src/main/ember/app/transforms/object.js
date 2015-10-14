import DS from 'ember-data';
import Ember from 'ember';

export default DS.Transform.extend({
    serialize: function (value) {
        console.log("Serializing:");
        console.log(value);

        if (Ember.isNone(value)) {
            console.log('None');
            return  {};
        }

        var propertyNames = Ember.keys(value) || [];
        console.log(propertyNames);

        return value.getProperties(propertyNames);
    },
    deserialize: function (value) {
        var obj = Ember.Object.create();
        if (!Ember.isNone(value)) {
            obj.setProperties(value);
        }
        return obj;
    }
});
