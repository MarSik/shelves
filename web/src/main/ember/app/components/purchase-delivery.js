import Ember from 'ember';

export default Ember.Component.extend({
    actions: {
        startDelivery: function() {
            this.set('delivering', true);
            this.set('count', this.get('purchase.missing'));
        },
        delivered: function() {
            this.set('delivering', false);
            this.sendAction('action', this.get('purchase'), this.get('location'), this.get('count'));
        }
    },
    delivering: false,
    fullyDelivered: function() {
        return this.get('purchase.fullyDelivered');
    }.property('purchase.fullyDelivered'),
    isDelivering: function () {
        return this.get('delivering') && !this.get('fullyDelivered');
    }.property('delivering', 'fullyDelivered'),
    canDeliver: function () {
        return !this.get('delivering') && !this.get('fullyDelivered');
    }.property('delivering', 'fullyDelivered'),
    buttonDisabled: function() {
        return Ember.isEmpty(this.get('location'));
    }.property('location')
});
