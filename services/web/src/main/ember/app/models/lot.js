import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  count: attr("number"),
  created: attr("date"),

  location: belongsTo("box", {async: true}),

  previous: belongsTo("lot", {inverse: "next", async: true}),
  next: hasMany("lot", {inverse: "previous", async: true})
});
