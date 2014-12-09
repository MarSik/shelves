import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr(),

    types: hasMany("type", {async: true}),

    parent: belongsTo("group", {inverse: "groups", async: true}),
    groups: hasMany("group", {inverse: "parent", async: true})
});
