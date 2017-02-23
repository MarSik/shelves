import Ember from 'ember';
import DS from 'ember-data';
import LotBase from './lotbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default LotBase.extend({
  type: belongsTo('type', {inverse: null, async: true}),

  singlePrice: attr("number"),
  totalPrice: attr("number"),
  currency: attr('string'),

  singlePricePaid: attr("number"),
  totalPricePaid: attr("number"),
  currencyPaid: attr('string'),

  vat: attr("number"),
  sku: attr('string'),
  vatIncluded: attr("boolean"),
  transaction: belongsTo("transaction", {async: true}),
  lots: hasMany("lot", {inverse: null, async: true, polymorphic: true}),
  validLots: Ember.computed.filterBy("lots", "valid", true),

  /*missing: Ember.computed('delivered', 'count', function () {
      return this.get('count') - this.get('delivered')
  }),*/

  missing: attr("number"),

  priceWithVat: function () {
      if (!this.get('singlePrice')) {
        return 0;
      }

      if (this.get('vatIncluded')) {
          return this.get('singlePrice');
      } else {
          return (100.0 + this.get('vat')) * this.get('singlePrice') / 100.0;
      }
  }.property('vatIncluded', 'singlePrice', 'vat'),

  priceWithoutVat: function () {
    if (!this.get('singlePrice')) {
      return 0;
    }

      if (!this.get('vatIncluded')) {
          return this.get('singlePrice');
      } else {
          return this.get('singlePrice') / ((100.0 + this.get('vat')) / 100.0);
      }
  }.property('vatIncluded', 'singlePrice', 'vat'),

  _lots: Ember.computed('lots.@each.valid', function () {
    const promise = this.get('lots').then(lots => {
      return lots.filterBy('valid');
    });

    return DS.PromiseArray.create({
      promise: promise
    });
  }),

  delivered: function () {
      let count = 0;

      this.get('_lots').forEach(function (lot) {
          count += lot.get('count');
      });

      return count;
  }.property('_lots.@each.count'),

  fullyDelivered: function() {
      return this.get('missing') <= 0;
  }.property('missing')
});
