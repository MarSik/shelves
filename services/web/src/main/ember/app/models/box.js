import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  name: attr(),
  lots: hasMany("lot", {async: true}),
  parent: belongsTo("box", {async: true, inverse: "boxes"}),
  boxes: hasMany("box", {async: true, inverse: "parent"}),
  code: belongsTo("code", {async: true})
});
