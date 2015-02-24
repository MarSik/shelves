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

        this.get('items').forEach(function (item) {
            if (!item.get('fullyDelivered')) {
                ready = false;
            }
        });

        return ready;
    }.property('items.@each.fullyDelivered'),

    missing: function () {
        var missing = 0;

        this.get('items').forEach(function (item) {
            missing += item.get('missing');
        });

        return missing;
    }.property('items.@each.missing'),

    link: function() {
        return "transactions.show";
    }.property()
});
