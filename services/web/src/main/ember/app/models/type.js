import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  name: attr("string"),
  groups: hasMany("group", {async: true}),
  footprint: belongsTo("footprint", {async: true}),
  lots: hasMany("lot", {async: true}),
  belongsTo: belongsTo("user", {async: true})
});
