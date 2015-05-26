import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        delivered: function (purchase, location, count) {
            console.log('delivering...');
            var lot = this.store.createRecord('lot', {
                purchase: purchase,
                location: location,
                count: count
            });

            lot.save();
        },
        fixVat: function (purchase) {
            purchase.set('vatIncluded', !purchase.get('vatIncluded'));
            purchase.save();
        }
    },
    setupController: function(controller, model) {
        controller.set('model', model);
        controller.set('boxes', this.store.find('box'));
    }
});
