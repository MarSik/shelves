import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    prefix: attr('string'),
    power10: attr(),
    units: hasMany('unit', {async: true})
});