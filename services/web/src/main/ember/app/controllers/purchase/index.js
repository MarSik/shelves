import Ember from 'ember';

export default Ember.Controller.extend({
    needs: "application",
    actions: {
        addRow: function () {
            this.store.createRecord('purchase', {
                vat: 20,
                vatIncluded: true,
                transaction: this.get('model')
            });
        }
    },

    selectedSource: null,

    typeSorting: ['name'],
    sortedTypes: Ember.computed.sort('controllers.application.availableTypes', 'typeSorting'),
    sourceSorting: ['name'],
    sortedSources: Ember.computed.sort('controllers.application.availableSources', 'sourceSorting'),
    submitDisabled: function () {
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
    }.property('model.source.content', 'model.items.length', 'model.items.@each.type.content', 'model.items.@each.count')
});
