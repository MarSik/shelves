import Ember from 'ember';
import DS from 'ember-data';
import IdentifiedBase from './identifiedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default IdentifiedBase.extend({
  version: attr(),
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

    totalWithVat: Ember.computed('items.@each.priceWithVat', 'items.@each.count', {
        set(key, value) {
            return value;
        },
        get() {
            var self = this;
            this.get('items').then(function (ts) {
                var total = 0;
                ts.forEach(function (item) {
                    total += item.get('priceWithVat') * item.get('count');
                });
                self.set('totalWithVat', total);
            });

            return 0;
        }
    }),

    totalWithoutVat: Ember.computed('items.@each.priceWithoutVat', 'items.@each.count', {
        set(key, value) {
            return value;
        },
        get() {
            var self = this;
            this.get('items').then(function (ts) {
                var total = 0;
                ts.forEach(function (item) {
                    total += item.get('priceWithoutVat') * item.get('count');
                });
                self.set('totalWithoutVat', total);
            });

            return 0;
        }
    }),

    deliveredWithVat: Ember.computed('items.@each.priceWithVat', 'items.@each.delivered', {
        set(key, value) {
            return value;
        },
        get() {
            var self = this;
            this.get('items').then(function (ts) {
                var total = 0;
                ts.forEach(function (item) {
                    total += item.get('priceWithVat') * item.get('delivered');
                });
                self.set('deliveredWithVat', total);
            });

            return 0;
        }
    }),

    deliveredWithoutVat: Ember.computed('items.@each.priceWithoutVat', 'items.@each.delivered', {
        set(key, value) {
            return value;
        },
        get() {
            var self = this;
            this.get('items').then(function (ts) {
                var total = 0;
                ts.forEach(function (item) {
                    total += item.get('priceWithoutVat') * item.get('delivered');
                });
                self.set('deliveredWithoutVat', total);
            });

            return 0;
        }
    }),

    missingWithVat: Ember.computed('totalWithVat', 'deliveredWithVat', function () {
       return this.get('totalWithVat') - this.get('deliveredWithVat');
    }),

    missingWithoutVat: Ember.computed('totalWithoutVat', 'deliveredWithoutVat', function () {
        return this.get('totalWithoutVat') - this.get('deliveredWithoutVat');
    }),

    link: function() {
        return "transactions.show";
    }.property(),

    icon: function () {
        return "shopping-cart";
    }.property()
});
