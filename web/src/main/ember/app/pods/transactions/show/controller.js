import Ember from 'ember';

export default Ember.Controller.extend({
    application: Ember.inject.controller("application"),
    actions: {
        delivered: function () {
            console.log("delivering ..");
            return true;
        },

        editPrice(purchase) {
            this.set('selectedItem', purchase);
        },

        updatePrice() {
            var purchase = this.get('selectedItem');

            purchase.set('currency', this.get('invoiceCurrency'));
            purchase.set('currencyPaid', this.get('paidCurrency'));

            purchase.set('singlePricePaid', this.get('exchangeRate') * purchase.get('singlePrice'));
            purchase.set('totalPrice', purchase.get('count') * purchase.get('singlePrice'));
            purchase.set('totalPricePaid', this.get('exchangeRate') * purchase.get('totalPrice'));

            var self = this;
            purchase.save().then(function () {
              self.set('selectedItem', null);
            });
        },

      cancelPriceChange() {
        var purchase = this.get('selectedItem');
        purchase.rollbackAttributes();
        this.set('selectedItem', null);
      }
    },
    boxSorting: ['fullName'],
    allBoxes: [],
    sortedBoxes: Ember.computed.sort('allBoxes', 'boxSorting'),
    itemSorting: ['type.name'],
    sortedItems: Ember.computed.sort('model.items', 'itemSorting'),
    location: null,
    editEnabled: Ember.computed('model.locked', function () {
        return !this.get('model.locked');
    }),

    selectedItem: null,
    exchangeRateDefault: 1.00,
    exchangeRate: Ember.computed('selectedItem.singlePrice', 'selectedItem.singlePricePaid', {
      get(key) {
        var singlePrice = this.get('selectedItem.singlePrice');
        var singlePricePaid = this.get('selectedItem.singlePricePaid');

        if (!Ember.isEmpty(singlePrice) && !Ember.isEmpty(singlePricePaid)) {
          this.set('exchangeRateDefault', singlePricePaid / singlePrice);
          return singlePricePaid / singlePrice;
        } else {
          return this.get('exchangeRateDefault');
        }
      },
      set(key, value) {
        this.set('exchangeRateDefault', value);
      }
    }),

    selectedItemPaidPrice: Ember.computed('selectedItem.singlePrice', 'selectedItem', 'exchangeRate', function () {
      var purchase = this.get('selectedItem');
      var exchangeRate = this.get('exchangeRate');

      if (Ember.isEmpty(purchase)) return null;

      return purchase.get('singlePrice') * exchangeRate;
    }),

    invoiceCurrencyDefault: "",
    invoiceCurrency: Ember.computed('selectedItem.currency', {
      get(key) {
        var modelValue = this.get('selectedItem.currency');
        if (!Ember.isEmpty(modelValue)) {
          this.set('invoiceCurrencyDefault', modelValue);
          return modelValue;
        } else {
          return this.get('invoiceCurrencyDefault');
        }
      },
      set(key, value) {
        this.set('invoiceCurrencyDefault', value);
        this.set('selectedItem.currency', value);
      }
    }),

    paidCurrencyDefault: "",
    paidCurrency: Ember.computed('selectedItem.currencyPaid', {
      get(key) {
        var modelValue = this.get('selectedItem.currencyPaid');
        if (!Ember.isEmpty(modelValue)) {
          this.set('paidCurrencyDefault', modelValue);
          return modelValue;
        } else {
          return this.get('paidCurrencyDefault');
        }
      },
      set(key, value) {
        this.set('paidCurrencyDefault', value);
        this.set('selectedItem.currencyPaid', value);
      }
    }),
});
