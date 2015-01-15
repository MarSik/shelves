import Ember from 'ember';

export default Ember.Route.extend({
    model: function () {
        if (this.get('currentModel') && this.get('currentModel.isNew')) {
            return this.get('currentModel');
        }

        return this.store.createRecord('transaction');
    }
});
