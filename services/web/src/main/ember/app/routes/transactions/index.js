import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        purchase: function (transaction) {
            var self = this;
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

        return this.store.createRecord('transaction');
    }
});
