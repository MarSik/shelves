import DS from 'ember-data';

var proxy = function (param, def) {
  return Ember.computed("object", "object."+param, function () {
    var obj = this.get('object.content');
    if (Ember.isEmpty(obj)) {
      return def;
    } else {
      return obj.get(param);
    }
  });
};

export default DS.Model.extend({
	object: DS.belongsTo('identifiedbase', {polymorphic: true, async: true, inverse: null}),

  stickerId: proxy('id', "00000000-0000-0000-0000-000000000000"),
  icon: proxy('icon', "question"),
  fullName: proxy('fullName', "New sticker")
});
