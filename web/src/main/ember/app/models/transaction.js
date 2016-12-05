import Ember from 'ember';
import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

function reduceItems(dependsOn, reducer, defaultValue) {
    var acc = defaultValue;
    this.get('_items').forEach(function (item) {
      acc = reducer(acc, item);
    });
    return acc;
};

export default NamedBase.extend({
  version: attr(),
  name: attr('string'),
    date: attr('date'),
    expectedDelivery: attr('date'),
    items: hasMany("purchase", {async: true}),
    belongsTo: belongsTo("user", {async: true}),
    source: belongsTo("source", {async: true}),

    // UI specific field
    locked: attr('boolean', {serialize: false, defaultValue: true}),

    _items: Ember.computed('items.@each', {
      const promise = this.get('items').then(items => {
        return items;
      });

      return DS.PromiseArray.create({
        promise: promise
      });
    }),

    // needed for sticker test
    types: Ember.computed('_items.@each.type', function () {
      var types = [];
      this.get('_items').forEach(function (p) {
        types.pushObject(p.get('type'));
      });
      return types;
    }),

    fullyDelivered: Ember.computed('_items.@each.fullyDelivered', function () {
      var ready = true;
      this.get('items').forEach(function (item) {
        if (!item.get('fullyDelivered')) {
          ready = false;
        }
      });
      return ready;
    }),

    missing: Ember.computed('_items.@each.missing', function () {
      let missing = 0;
      this.get('_items').forEach(function (item) {
        missing += item.get('missing');
      });
      return missing;
    }),

  totalCount: Ember.computed('_items.@each.count', function () {
    var count = 0;
    this.get('_items').forEach(function (item) {
      count += item.get('count');
    });
    return count;
  }),

  deliveredCount: Ember.computed('items.@each.delivered', function () {
    var count = 0;
    this.get('_items').forEach(function (item) {
      count += item.get('delivered');
    });
    return count;
  }),

  totalWithVat: Ember.computed('items.@each.priceWithVat', 'items.@each.count', function () {
    var total = 0;
    this.get("_items").forEach(function (item) {
        total += item.get('priceWithVat') * item.get('count');
    });
    return total;
  }),

  totalWithoutVat: Ember.computed('items.@each.priceWithoutVat', 'items.@each.count', function () {
    var total = 0;
    this.get("_items").forEach(function (item) {
        total += item.get('priceWithoutVat') * item.get('count');
    });
    return total;
  }),

  deliveredWithVat: Ember.computed('items.@each.priceWithVat', 'items.@each.delivered', function () {
    var total = 0;
    this.get("_items").forEach(function (item) {
        total += item.get('priceWithVat') * item.get('delivered');
    });
    return total;
  }),

  deliveredWithoutVat: Ember.computed('items.@each.priceWithoutVat', 'items.@each.delivered', function () {
    var total = 0;
    this.get("_items").forEach(function (item) {
        total += item.get('priceWithoutVat') * item.get('delivered');
    });
    return total;
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
