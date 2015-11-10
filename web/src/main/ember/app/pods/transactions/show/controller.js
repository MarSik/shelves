import Ember from 'ember';

export default Ember.Controller.extend({
    needs: "application",
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

            purchase.set('singlePricePaid', this.get('exchangeRate') * purchase.get('singlePrice'));
            purchase.set('totalPrice', purchase.get('count') * purchase.get('singlePrice'));
            purchase.set('totalPricePaid', this.get('exchangeRate') * purchase.get('totalPrice'));

            var self = this;
            purchase.save().then(function () {
              self.set('selectedItem', null);
            });
        }
    },
    boxSorting: ['fullName'],
    sortedBoxes: Ember.computed.sort('controllers.application.availableLocations', 'boxSorting'),
    itemSorting: ['type.name'],
    sortedItems: Ember.computed.sort('model.items', 'itemSorting'),
    location: null,
    editEnabled: Ember.computed('model.locked', function () {
        return !this.get('model.locked');
    }),

    selectedItem: null,
    exchangeRate: 1.00,

    selectedItemPaidPrice: Ember.computed('selectedItem', 'exchangeRate', function () {
      var purchase = this.get('selectedItem');
      var exchangeRate = this.get('exchangeRate');

      if (Ember.isEmpty(purchase)) return null;

      return purchase.get('singlePrice') * exchangeRate;
    })
});
