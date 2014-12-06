import Ember from 'ember';
import Authenticator from 'simple-auth-oauth2/authenticators/oauth2';

export default Authenticator.extend({
    makeRequest: function (url, data) {
        var client_id = 'elshelves.js';
        var client_secret = 'public';
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
