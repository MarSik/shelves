import Ember from 'ember';

export default Ember.Controller.extend({
    pendingTransactions: Ember.computed.filterBy('model', 'fullyDelivered', false)
});
