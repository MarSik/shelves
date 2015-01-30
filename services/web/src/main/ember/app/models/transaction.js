import DS from 'ember-data';
import Ember from 'ember';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr('string'),
    date: attr('date'),
    items: hasMany("purchase", {async: true}),
    belongsTo: belongsTo("user", {async: true}),
    source: belongsTo("source", {async: true}),

    // needed for sticker test
    types: function() {
        var types = [];
        this.get('items').forEach(function (p) {
            types.pushObject(p.get('type'));
        });
        return types;
    }.property('items.@each.type'),

    fullyDelivered: function() {
        var ready = true;

        this.get('items').then(function (items) {
           items.forEach(function (item) {
               ready = ready && item.get('fullyDelivered');
           });
        });

        return ready;
    }.property('items.@each.fullyDelivered')
});
