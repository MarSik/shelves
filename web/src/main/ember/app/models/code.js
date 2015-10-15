import DS from 'ember-data';

var attr = DS.attr,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    code: attr('string'),
    type: attr('string'),
    reference: belongsTo('namedbase', {async: true, polymorphic: true})
});