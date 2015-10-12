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
            if (serialized[k] === null) {
                delete serialized[k];
            }
        });

        //Remove the root element
        Ember.merge(hash, serialized);
    },
    extractMeta: function (store, type, payload) {
        // Read the meta information about objects that should be
        // reloaded - purge them from the store to force reload
        if (payload && payload.meta && payload.meta.purge) {
            payload.meta.purge.forEach(function (p) {
                var entity = store.getById(p.type, p.id);
                if (!Ember.isNone(entity)) {
                    Ember.run.next(entity, "reload");
                }
            });
        }

        return this._super(store, type, payload);
    }
});
