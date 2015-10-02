import Ember from 'ember';

export default Ember.Controller.extend({
    needs: ["application", "transactions"],
    actions: {
        addRow: function () {
            this.store.createRecord('purchase', {
                vat: 20,
                vatIncluded: true,
                transaction: this.get('model')
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
        createType: function (name) {
            var type = this.store.createRecord('type', {
                name: name,
                flagged: true
            });

            type.save().catch(function (err) {
                this.growl.error(err);
                type.destroy();
            });
        }
    },
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

    pendingTransactions: Ember.computed.filterBy('controllers.transactions.model', 'fullyDelivered', false)
});
