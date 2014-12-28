import Ember from 'ember';

export default Ember.Route.extend({
    model: function () {
        return  this.store.filter('purchase', function (purchase) {
            return purchase.get('isNew');
        });
    },
    setupController: function(controller, model) {
        controller.set('model', model);
        controller.set('sources', this.store.find('source'));
        controller.set('boxes', this.store.find('box'));
        controller.set('types', this.store.find('type'));
    }
});
