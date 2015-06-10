import Ember from 'ember';
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

    // UI specific field
    locked: attr('boolean', {serialize: false, defaultValue: true}),

    // needed for sticker test
    types: Ember.computed('items.@each.type', {
        set(key, value) {
            return value;
        },
        get() {
          var self = this;
          this.get('items').then(function (ts) {
            var types = [];
            ts.forEach(function (p) {
              types.pushObject(p.get('type'));
            });
            self.set('types', types);
          });
          return [];
        }
    }),

    fullyDelivered: Ember.computed('items.@each.fullyDelivered', {
        set(key, value) {
            return value;
        },
        get() {
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
        }
    }),

    missing: Ember.computed('items.@each.missing', {
        set(key, value) {
            return value;
        },
        get() {
          var self = this;
          this.get('items').then(function (ts) {
            var missing = 0;
            ts.forEach(function (item) {
              missing += item.get('missing');
            });
            self.set('missing', missing);
          });

          return 0;
        }
    }),

    link: function() {
        return "transactions.show";
    }.property(),

    icon: function () {
        return "shopping-cart";
    }.property()
});
