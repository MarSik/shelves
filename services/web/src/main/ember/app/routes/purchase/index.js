import Ember from 'ember';

export default Ember.Route.extend({
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
