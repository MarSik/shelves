import Ember from 'ember';
import ENV from '../../config/environment';
/* global $ */

export default Ember.Controller.extend({
    app: Ember.inject.controller('application'),
    session: Ember.inject.service('session'),
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
        },
        authenticate() {
            var data = this.getProperties('identification', 'password');
            console.log("Auth: "+JSON.stringify(data));
            this.get('session').authenticate(this.get('authenticator'), this.get('identification'), this.get('password'));
        }
    },
    authenticator: 'authenticator:application'
});
