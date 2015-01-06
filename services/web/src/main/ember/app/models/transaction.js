import DS from 'ember-data';
import Ember from 'ember';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr('string'),
    date: attr('date'),
    items: hasMany("purchase", {async: true}),
    belongsTo: belongsTo("user"),
    source: belongsTo("source", {async: true})
});
