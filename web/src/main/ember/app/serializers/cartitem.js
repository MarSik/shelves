import Ember from 'ember';

var ShelvesStickerSerializer = DS.JSONSerializer.extend({
  isNewSerializerAPI: true,

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
      json[payloadKey] = {id: belongsToId, type: belongsTo.modelName};
    }
  }
});

Ember.runInDebug(function() {
  ShelvesStickerSerializer.reopen({
    warnMessageNoModelForType: function(modelName, originalType) {
      return 'Encountered a resource object with type "' + originalType + '", but no model was found for model name "' + modelName + '" (resolved model name using ' + this.constructor.toString() + '.modelNameFromPayloadKey("' + originalType + '"))';
    }
  });
});

export default ShelvesStickerSerializer;
