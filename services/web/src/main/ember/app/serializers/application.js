import Ember from 'ember';
import DS from 'ember-data';

// Adapted from http://springember.blogspot.com.au/2014/08/using-ember-data-restadapter-with.html
export default DS.RESTSerializer.extend({
    serializeIntoHash: function(hash, type, record, options) {
        var serialized = this.serialize(record, options);

        //Remove id from the payload for new records
        //Jackson was complaining when it received a null id ...
        if (record.id == null) {
            delete serialized.id;
        }

        //remove the root element
        Ember.merge(hash, serialized);
    }
});
