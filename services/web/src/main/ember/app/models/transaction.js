import DS from 'ember-data';
import Ember from 'ember';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr('string'),
    date: attr(),
    items: hasMany("purchase"),
    belongsTo: belongsTo("user"),
    source: belongsTo("source")
});
