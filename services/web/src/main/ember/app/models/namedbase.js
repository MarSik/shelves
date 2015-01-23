import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr('string'),
    description: attr('string'),
    summary: attr(),

    belongsTo: belongsTo('user', {async: true}),
    describedBy: hasMany("document", {async: true}),

    values: hasMany('propertyvalue', {inverse: null})
});
