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

            lot.save().then(function (l) {
                // Add the lot to the purchase explicitly to workaround polymorphic issues
                // and implicit references from invalid lots
                purchase.get('lots').pushObject(l);
            }).catch(function (e) {
                newDoc.rollback();
                self.growl.error('Delivery request failed: '+e);
            });
        },
        fixVat: function (purchase) {
            purchase.set('vatIncluded', !purchase.get('vatIncluded'));
            purchase.save();
        },
        lock(model) {
            model.set("locked", true);
        },
        unlock(model) {
          model.set("locked", false);
        }
    },
    afterModel() {
      this.store.findAll('box');
    }
});
