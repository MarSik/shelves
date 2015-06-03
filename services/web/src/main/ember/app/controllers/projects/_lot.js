import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        showActions: function () {
            this.set('displayActions', true);
        },
        hideActions: function () {
            this.set('displayActions', false);
        },
        solderLot: function () {
            this.set('displayActions', false);
            return true;
        },
        unsolderLot: function () {
            this.set('displayActions', false);
            return true;
        },
        unassignLot: function () {
            this.set('displayActions', false);
            return true;
        }
    },
    displayActions: false
});
