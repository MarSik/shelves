import DS from 'ember-data';

var attr = DS.attr,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  version: attr(),
  count: attr("number"),
    created: attr("date"),

    belongsTo: belongsTo("user")
});
