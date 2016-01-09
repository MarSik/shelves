import Ember from 'ember';
import DS from 'ember-data';

const dasherize = Ember.String.dasherize;

var ShelvesSerializer = DS.JSONSerializer.extend({
  isNewSerializerAPI: true,
  normalizeResponse(store, modelClass, payload, id, requestType) {
    this.extractMeta(store, modelClass, payload);
    return this._super(store, modelClass, payload, id, requestType);
  },

    serializeIntoHash: function(hash, type, record, options) {
        var serialized = this.serialize(record, options);
        serialized["_type"] = type.modelName;

        console.log("SER", serialized);

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

  serializePolymorphicType: function(snapshot, json, relationship) {
    var key = relationship.key,
        belongsTo = snapshot.belongsTo(key),
        belongsToId = snapshot.belongsTo(key, { id: true });

    // if provided, use the mapping provided by `attrs` in
    // the serializer
    var payloadKey = this._getMappedKey(key);
    if (payloadKey === key && this.keyForRelationship) {
      payloadKey = this.keyForRelationship(key, "belongsTo", "serialize");
    }

    if (!Ember.isNone(belongsTo)) {
      json[payloadKey] = {id: belongsToId, _type: belongsTo.modelName};
    }
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
    },

  /**
   Check if the passed model has a `type` attribute or a relationship named `type`.
   @method modelHasAttributeOrRelationshipNamedType
   @param modelClass
   */
  modelHasAttributeOrRelationshipNamedType(modelClass) {
    return Ember.get(modelClass, 'attributes').has('type') || Ember.get(modelClass, 'relationshipsByName').has('type');
  },

  // Use hash._type instead of hash.type to detect the relationship type
  extractRelationship: function(relationshipModelName, relationshipHash) {
    if (Ember.isNone(relationshipHash)) { return null; }
    /*
     When `relationshipHash` is an object it usually means that the relationship
     is polymorphic. It could however also be embedded resources that the
     EmbeddedRecordsMixin has be able to process.
     */
    if (Ember.typeOf(relationshipHash) === 'object') {
      if (relationshipHash.id) {
        relationshipHash.id = this.coerceId(relationshipHash.id);
      }

      const modelClass = this.store.modelFor(relationshipModelName);
      if (relationshipHash._type && !this.modelHasAttributeOrRelationshipNamedType(modelClass)) {
        relationshipHash._type = this.modelNameFromPayloadKey(relationshipHash._type);
      }

      return { id: relationshipHash.id, type: relationshipHash._type };
    }

    return { id: this.coerceId(relationshipHash), type: relationshipModelName };
  },

  /**
   @method modelNameFromPayloadKey
   @param {String} key
   @return {String} the model's modelName
   */
  modelNameFromPayloadKey: function(key) {
    return Ember.Inflector.inflector.singularize(DS.normalizeModelName(key));
  },

  /**
   @method keyForAttribute
   @param {String} key
   @param {String} method
   @return {String} normalized key
   */
  keyForAttribute: function(key, method) {
    return key;
  },

  /**
   `keyForRelationship` can be used to define a custom key when
   serializing and deserializing relationship properties.
   By default `JSONAPISerializer` follows the format used on the examples of
   http://jsonapi.org/format and uses dashes as word separators in
   relationship properties.
   This behaviour can be easily customized by extending this method.
   Example
   ```app/serializers/post.js
   import DS from 'ember-data';
   export default DS.JSONAPISerializer.extend({
      keyForRelationship: function(key, relationship, method) {
        return Ember.String.underscore(key);
      }
    });
   ```
   @method keyForRelationship
   @param {String} key
   @param {String} typeClass
   @param {String} method
   @return {String} normalized key
   */
  keyForRelationship: function(key, typeClass, method) {
    return key;
  },


  // Used by the store to normalize IDs entering the store.  Despite the fact
  // that developers may provide IDs as numbers (e.g., `store.find(Person, 1)`),
  // it is important that internally we use strings, since IDs may be serialized
  // and lose type information.  For example, Ember's router may put a record's
  // ID into the URL, and if we later try to deserialize that URL and find the
  // corresponding record, we will not know if it is a string or a number.
  coerceId(id) {
    return id == null || id === '' ? null : id+'';
  },

  /**
   @method _normalizeResponse
   @param {DS.Store} store
   @param {DS.Model} primaryModelClass
   @param {Object} payload
   @param {String|Number} id
   @param {String} requestType
   @param {Boolean} isSingle
   @return {Object} JSON-API Document
   @private
   */
  _normalizeResponse: function(store, primaryModelClass, payload, id, requestType, isSingle) {
    let normalizedPayload = this._normalizeDocumentHelper(payload);
    return normalizedPayload;
  },

  pushPayload(store, payload) {
    store.push(this._normalizeDocumentHelper(payload));
  },

  /**
   @method _normalizeDocumentHelper
   @param {Object} documentHash
   @return {Object}
   @private
   */
  _normalizeDocumentHelper: function(documentHash) {

    if (Ember.typeOf(documentHash.data) === 'object') {
      documentHash.data = this._normalizeResourceHelper(documentHash.data);
    } else if (Ember.typeOf(documentHash.data) === 'array') {
      documentHash.data = documentHash.data.map(this._normalizeResourceHelper, this);
    }

    if (Ember.typeOf(documentHash.included) === 'array') {
      documentHash.included = documentHash.included.map(this._normalizeResourceHelper, this);
    }

    return documentHash;
  },

  _normalizeResourceHelper: function(resourceHash) {
    let modelName = this.modelNameFromPayloadKey(resourceHash._type);
    let modelClass = this.store.modelFor(modelName);

    if (Ember.isEmpty(modelClass)) {
      console.log("ERROR: Unknown model for ", resourceHash);
    }

    let serializer = this.store.serializerFor(modelName);
    let { data } = serializer.normalize(modelClass, resourceHash);
    return data;
  }
});

Ember.runInDebug(function() {
  ShelvesSerializer.reopen({
    warnMessageNoModelForType: function(modelName, originalType) {
      return 'Encountered a resource object with type "' + originalType + '", but no model was found for model name "' + modelName + '" (resolved model name using ' + this.constructor.toString() + '.modelNameFromPayloadKey("' + originalType + '"))';
    }
  });
});

export default ShelvesSerializer;
