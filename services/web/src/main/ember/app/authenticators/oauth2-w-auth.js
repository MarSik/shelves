import Ember from 'ember';
import Authenticator from 'simple-auth-oauth2/authenticators/oauth2';
import ENV from '../config/environment';

export default Authenticator.extend({
    makeRequest: function (url, data) {
        var client_id = ENV['simple-auth']['client-id'];
        var client_secret = ENV['simple-auth']['client-secret'];
        data.grant_type = "password";

        return Ember.$.ajax({
            url: url,
            type: 'POST',
            data: data,
            dataType: 'json',
            contentType: 'application/x-www-form-urlencoded',
            crossDomain: true,
            headers: {
                Authorization: "Basic " + btoa(client_id + ":" + client_secret)
            }
        });
    }
});
