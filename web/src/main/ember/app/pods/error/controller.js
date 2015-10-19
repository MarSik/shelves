import Ember from 'ember';

export default Ember.Controller.extend({
    reason: function() {
        return JSON.stringify(this.get('model'), null, 2);
    }.property()
});
