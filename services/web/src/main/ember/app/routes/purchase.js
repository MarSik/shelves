import Ember from 'ember';

export default Ember.Route.extend({
    model: function () {
        var transactions = this.store.filter('transaction', function (purchase) {
            return purchase.get('isNew');
        });

        if (transactions.get("length") == 0) {
            return this.store.createRecord('transaction')
        }

        return transactions.get(0);
    },
    setupController: function(controller, model) {
        controller.set('model', model);
        controller.set('sources', this.store.find('source'));
        controller.set('boxes', this.store.find('box'));
        controller.set('types', this.store.find('type'));
    }
});
