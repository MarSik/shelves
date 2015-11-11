import Ember from 'ember';

export default Ember.Controller.extend({
    needs: ["application", "transactions"],
    actions: {
        addRow: function () {
            this.store.createRecord('purchase', {
                vat: this.get('defaultVat'),
                vatIncluded: this.get('defaultVatIncluded'),
                transaction: this.get('model'),
                currency: this.get('invoiceCurrency'),
                currencyPaid: this.get('paidCurrency')
            });
        },
        removeRow: function (purchase) {
            this.get('model.items').removeObject(purchase);
        },
        createSource: function (name) {
            var source = this.store.createRecord('source', {
                name: name,
                flagged: true
            });

            source.save().catch(function (err) {
                this.growl.error(err);
                source.destroy();
            });
        },
        createType: function (name, destination) {
            var type = this.store.createRecord('type', {
                name: name,
                flagged: true
            });

            console.log("Set ", destination);

            type.save().then(function (t) {
                destination.set('type', t);
            }).catch(function (err) {
                this.growl.error(err);
                type.destroy();
            });
        },
        showAll: function () {
          this.set('pendingOnly', false);
        },
        showPending: function() {
          this.set('pendingOnly', true);
        }
    },
    pendingOnly: true,
    selectedSource: null,

    typeSorting: ['name'],
    sortedTypes: Ember.computed.sort('controllers.application.availableTypes', 'typeSorting'),
    sourceSorting: ['name'],
    sortedSources: Ember.computed.sort('controllers.application.availableSources', 'sourceSorting'),
    submitDisabled: function () {
        if (Ember.isEmpty(this.get('model.name'))) {
            return true;
        }

        if (!this.get('model.source.content')) {
            return true;
        }

        if (this.get('model.items.length') < 1) {
            return true;
        }

        if (this.get('model.items').any(function (item) {
            return (item.get('type.content') == null || item.get('count') == null || item.get('count') < 1);
        })) {
            return true;
        }

        return false;
    }.property('model.name', 'model.source.content', 'model.items.length', 'model.items.@each.type.content', 'model.items.@each.count'),

    pendingTransactions: Ember.computed("pendingOnly", "controllers.transactions.model.@each.fullyDelivered",
      function () {
        var trs = this.get('controllers.transactions.model');

        if (this.get('pendingOnly')) {
          return trs.filterBy('fullyDelivered', false).sortBy('date');
        } else {
          return trs.sortBy('date');
        }
    }),

  defaultVat: 21,
  defaultVatIncluded: false,
  exchangeRate: 1.00,
  invoiceCurrency: "CZK",
  paidCurrency: "CZK"
});
