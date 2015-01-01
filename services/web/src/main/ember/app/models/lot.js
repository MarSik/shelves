import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  count: attr("number"),
  created: attr("date"),

  location: belongsTo("box"),
  type: belongsTo("type"),

  previous: belongsTo("lot", {inverse: "next", async: true}),
  next: hasMany("lot", {inverse: "previous", async: true}),
  belongsTo: belongsTo("user"),

  action: attr(),
  performedBy: belongsTo('user', {inverse: null})
});
