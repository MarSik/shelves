import DS from 'ember-data';
var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr('string'),
    belongsTo: belongsTo('user')
});
