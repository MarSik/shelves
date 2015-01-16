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
                self.transitionTo('transactions.show', transaction);
            }).catch(function (reason) {
                console.log(reason);
            });
        }
    }
});
