import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    query: attr('string'),
    items: hasMany('namedbase', {async: true, polymorphic: true, inverse: null})
});
