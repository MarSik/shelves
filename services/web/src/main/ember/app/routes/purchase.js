import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        purchase: function (transaction) {
            var self = this;
            transaction.save().then(function (transaction) {
                // Delete the items that were saved using nested POST and ended up
                // duplicated.
                transaction.get('items').filterBy('isNew', true).forEach(function (item) {
                    item.destroyRecord();
                });
                self.transitionTo('purchase.show', transaction);
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
    },
    setupController: function(controller, model) {
        controller.set('model', model);
        controller.set('sources', this.store.find('source'));
        controller.set('boxes', this.store.find('box'));
        controller.set('types', this.store.find('type'));
    }
});
