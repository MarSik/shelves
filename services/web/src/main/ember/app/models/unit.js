import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr("string"),
    summary: attr("string"),
    description: attr("string"),

    symbol: attr("string"),
    prefixes: attr(),

    belongsTo: belongsTo("user", {async: true})
});