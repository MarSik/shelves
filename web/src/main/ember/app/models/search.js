import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany;

export default DS.Model.extend({
    query: attr('string'),
    items: hasMany('namedbase', {async: true, polymorphic: true, inverse: null})
});
