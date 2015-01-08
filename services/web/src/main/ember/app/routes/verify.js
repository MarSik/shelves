import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        verify: function(token) {
            this.transitionTo("verify.show", token);
        }
    }
});
