import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  name: attr(),
  kicad: attr(),

  pads: attr("number"),
  holes: attr("number"),
  npth: attr("number", {defaultValue: 0}),
  pitch: attr(),
  belongsTo: belongsTo('user')
});
