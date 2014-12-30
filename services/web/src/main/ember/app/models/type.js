import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  name: attr("string"),
  description: attr("string"),
  vendor: attr("string"),
  groups: hasMany("group", {async: true}),
  footprint: belongsTo("footprint", {async: true}),
  lots: hasMany("lot", {async: true}),
  belongsTo: belongsTo("user", {async: true}),

  fullName: function () {
      return this.get('name') + ' | ' + this.get('footprint.name') + ' | ' + this.get('vendor');
  }.property('name', 'footprint.name', 'vendor')
});
