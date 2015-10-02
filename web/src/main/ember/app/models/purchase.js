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
  vat: attr("number"),
  vatIncluded: attr("boolean"),
  transaction: belongsTo("transaction", {async: true}),
  next: hasMany("lot", {inverse: null, async: true}),

  missing: Ember.computed('delivered', 'count', function () {
      return this.get('count') - this.get('delivered')
  }),

  priceWithVat: function () {
      if (this.get('vatIncluded')) {
          return this.get('singlePrice');
      } else {
          return (100.0 + this.get('vat')) * this.get('singlePrice') / 100.0;
      }
  }.property('vatIncluded', 'singlePrice', 'vat'),

  priceWithoutVat: function () {
      if (!this.get('vatIncluded')) {
          return this.get('singlePrice');
      } else {
          return this.get('singlePrice') / ((100.0 + this.get('vat')) / 100.0);
      }
  }.property('vatIncluded', 'singlePrice', 'vat'),

  delivered: function () {
      var count = 0;

      this.get('next').forEach(function (lot) {
          count += lot.get('count');
      });

      return count;
  }.property('next.@each.count'),

  fullyDelivered: function() {
      return this.get('missing') === 0;
  }.property('missing')
});
