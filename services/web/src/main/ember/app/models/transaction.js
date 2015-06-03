import DS from 'ember-data';

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
    types: function(key, value) {
        if (arguments.length > 1) {
            return value;
        }

        var self = this;
        this.get('items').then(function (ts) {
            var types = [];
            ts.forEach(function (p) {
                types.pushObject(p.get('type'));
            });
            self.set('types', types);
        });
        return [];
    }.property('items.@each.type'),

    fullyDelivered: function(key, value) {
        if (arguments.length > 1) {
            return value;
        }

        var self = this;

        this.get('items').then(function (ts) {
            var ready = true;
            ts.forEach(function (item) {
                if (!item.get('fullyDelivered')) {
                    ready = false;
                }
            });
            self.set('fullyDelivered', ready);
        });

        return true;
    }.property('items.@each.fullyDelivered'),

    missing: function (key, value) {
        if (arguments.length > 1) {
            return value;
        }

        var self = this;
        this.get('items').then(function (ts) {
            var missing = 0;
            ts.forEach(function (item) {
                missing += item.get('missing');
            });
            self.set('missing', missing);
        });

        return 0;
    }.property('items.@each.missing'),

    link: function() {
        return "transactions.show";
    }.property(),

    icon: function () {
        return "shopping-cart";
    }.property()
});
