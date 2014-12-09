import Ember from 'ember';
import LoginControllerMixin from 'simple-auth/mixins/login-controller-mixin';

export default Ember.Controller.extend(LoginControllerMixin, {
    actions: {
        register: function() {
            var store = this.store;
            var newUser = store.createRecord('user', {
                email: this.get('email'),
                name: this.get('name')
            });

            var self = this;

            newUser
                .save()
                .then(function() {
                    self.growl.info("Please wait for an email to arrive and confirm your registration.");
                })
                .catch(function() {
                    self.growl.error("Registration failed. The administratior was informed.");
                });
        }
    },
    authenticator: 'oauth2-w-auth:oauth2-password-grant'
});
