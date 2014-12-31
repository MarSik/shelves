import DS from 'ember-data';

var attr = DS.attr,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: DS.attr('string'),
    url: DS.attr('string'),
    belongsTo: belongsTo('user')
});
