import Ember from 'ember';
import DS from 'ember-data';

// Adapted from http://springember.blogspot.com.au/2014/08/using-ember-data-restadapter-with.html
export default DS.RESTSerializer.extend({
    serializeIntoHash: function(hash, type, record, options) {
        var serialized = this.serialize(record, options);

        //Include the id in the payload
        //Jackson was complaining when it received a null id ...
        serialized.id = record.id ? record.id : null;

        //remove the root element
        Ember.merge(hash, serialized);
    }
});
