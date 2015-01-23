import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    value: attr(),
    fraction: attr(),
    fractionDivider: attr(),

    isoPrefix: belongsTo('isoprefix', {async: true}),
    property: belongsTo("property", {async: true})
});