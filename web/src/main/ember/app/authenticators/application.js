import Ember from 'ember';
import OAuth2PasswordGrant from 'ember-simple-auth/authenticators/oauth2-password-grant';
import ENV from '../config/environment';

export default OAuth2PasswordGrant.extend({
    serverTokenEndpoint: ENV.APP.API_SERVER + (ENV.APP.API_BASE ? '/' + ENV.APP.API_BASE : '') + '/oauth/token',

    makeRequest: function (url, data) {
        var client_id = ENV['ember-simple-auth']['client-id'];
        var client_secret = ENV['ember-simple-auth']['client-secret'];

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
