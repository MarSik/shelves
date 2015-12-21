import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        purchase: function (transaction, invoiceCurrency, paidCurrency, rate) {
            var self = this;

            transaction.get('items').forEach(function (item) {
              item.set('currency', invoiceCurrency);
              item.set('currencyPaid', paidCurrency);

              if (Ember.isEmpty(item.get('totalPrice'))) {
                item.set('totalPrice', item.get('singlePrice') * item.get('count'));
              }
              item.set('singlePricePaid', item.get('singlePrice') * rate);
              item.set('totalPricePaid', item.get('totalPrice') * rate);
            });

            transaction.save().then(function (transaction) {
                // Delete the items that were saved using nested POST and ended up
                // duplicated.
                transaction.get('items').filterBy('isNew', true).forEach(function (item) {
                    item.destroyRecord();
                });
                self.transitionTo('transactions.show', transaction);
            }).catch(function (reason) {
                console.log(reason);
            });
        }
    },
    model: function () {
        if (this.get('currentModel') && this.get('currentModel.isNew')) {
            return this.get('currentModel');
        }

        return this.store.createRecord('transaction', {
            date: new Date()
        });
    },
    afterModel() {
      this.store.findAll('type');
      this.store.findAll('source');
    }
});
