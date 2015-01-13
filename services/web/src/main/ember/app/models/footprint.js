import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  name: attr(),
  summary: attr(),
  description: attr(),
  kicad: attr(),

  pads: attr("number"),
  holes: attr("number"),
  npth: attr("number", {defaultValue: 0}),
  pitch: attr(),
  describedBy: hasMany("document", {async: true}),
  belongsTo: belongsTo('user', {async: true})
});
