import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    count: attr("number"),
    created: attr("date"),

    belongsTo: belongsTo("user")
});