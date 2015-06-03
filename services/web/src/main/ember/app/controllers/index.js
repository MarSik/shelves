import Ember from 'ember';
import LoginControllerMixin from 'simple-auth/mixins/login-controller-mixin';
import ENV from '../config/environment';
/* global $ */

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
                    self.growl.error("Registration failed. The administrator was informed.");
                });
        },
        reverify: function () {
            var email = this.get('identification');

            if (!email) {
                this.growl.error("Email must be provided!");
                return;
            }

            var url = ENV.APP.API_ENDPOINT + '/users/reverify/' + email;
            var self = this;
            $.ajax({
                url: url,
                type: "POST"
            }).then(function () {
                self.growl.info("Verification email was sent to the user this email (" +email+ ") belongs to. Use it to request a new password.");
            });
        }
    },
    authenticator: 'oauth2-w-auth:oauth2-password-grant'
});
