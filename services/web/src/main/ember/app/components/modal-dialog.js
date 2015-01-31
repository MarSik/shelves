import Ember from 'ember';

export default Ember.Component.extend({
    actions: {
        close: function () {
            this.set('condition', null);
        }
    }
    //condition,
    //title
});
