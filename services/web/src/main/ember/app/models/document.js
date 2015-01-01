import DS from 'ember-data';
var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr('string'),
    contentType: attr('string'),
    size: attr('number'),
    created: attr('date'),
    belongsTo: belongsTo('user')
});
