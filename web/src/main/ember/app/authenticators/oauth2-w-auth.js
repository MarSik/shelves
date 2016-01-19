import Ember from 'ember';
import Authenticator from 'simple-auth-oauth2/authenticators/oauth2';
import ENV from '../config/environment';

export default Authenticator.extend({
    makeRequest: function (url, data) {
        var client_id = ENV['simple-auth']['client-id'];
        var client_secret = ENV['simple-auth']['client-secret'];

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
    },

  /**
   Authenticates the session with the specified `options`; makes a `POST`
   request to the
   [`Authenticators.OAuth2#serverTokenEndpoint`](#SimpleAuth-Authenticators-OAuth2-serverTokenEndpoint)
   with the passed credentials and optional scope and receives the token in
   response (see http://tools.ietf.org/html/rfc6749#section-4.3).

   __If the credentials are valid (and the optionally requested scope is
   granted) and thus authentication succeeds, a promise that resolves with the
   server's response is returned__, otherwise a promise that rejects with the
   error is returned.

   This method also schedules automatic token refreshing when there are values
   for `refresh_token` and `expires_in` in the server response and automatic
   token refreshing is not disabled (see
   [`Authenticators.OAuth2#refreshAccessTokens`](#SimpleAuth-Authenticators-OAuth2-refreshAccessTokens)).

   @method authenticate
   @param {Object} options
   @param {String} options.identification The resource owner username
   @param {String} options.password The resource owner password
   @param {String|Array} [options.scope] The scope of the access request (see [RFC 6749, section 3.3](http://tools.ietf.org/html/rfc6749#section-3.3))
   @return {Ember.RSVP.Promise} A promise that resolves when an access token is successfully acquired from the server and rejects otherwise
   */
  authenticate: function(options) {
    var _this = this;
    return new Ember.RSVP.Promise(function(resolve, reject) {
      var data;

      if (Ember.isEmpty(options.grant_type)) {
        data = { grant_type: 'password', username: options.identification, password: options.password };
        if (!Ember.isEmpty(options.scope)) {
          var scopesString = Ember.makeArray(options.scope).join(' ');
          Ember.merge(data, { scope: scopesString });
        }
      } else {
        data = options;
      }

      _this.makeRequest(_this.serverTokenEndpoint, data).then(function(response) {
        Ember.run(function() {
          var expiresAt = _this.absolutizeExpirationTime(response.expires_in);
          _this.scheduleAccessTokenRefresh(response.expires_in, expiresAt, response.refresh_token);
          if (!Ember.isEmpty(expiresAt)) {
            response = Ember.merge(response, { expires_at: expiresAt });
          }
          resolve(response);
        });
      }, function(xhr, status, error) {
        Ember.run(function() {
          reject(xhr.responseJSON || xhr.responseText);
        });
      });
    });
  }
});
