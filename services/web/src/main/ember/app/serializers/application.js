import Ember from 'ember';
import DS from 'ember-data';

// Adapted from http://springember.blogspot.com.au/2014/08/using-ember-data-restadapter-with.html
export default DS.RESTSerializer.extend({
    serializeIntoHash: function(hash, type, record, options) {
        var serialized = this.serialize(record, options);

        //Remove id from the payload for new records
        //Jackson was complaining when it received a null or 'new' id ...
        if (record.get('id') == null || record.get('isNew')) {
            delete serialized.id;
        }

        //Remove null values
        Object.keys(serialized).forEach(function(k) {
            if (!serialized[k]) {
                delete serialized[k];
            }
        });

        //Remove the root element
        Ember.merge(hash, serialized);
    }
});
