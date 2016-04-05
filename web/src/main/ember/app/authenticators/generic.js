import Ember from 'ember';
import OAuth2PasswordGrant from 'ember-simple-auth/authenticators/oauth2-password-grant';
import ENV from '../config/environment';

const { RSVP, isEmpty, run, computed } = Ember;
const assign = Ember.assign || Ember.merge;

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
    },

  /**
    Authenticates the session with the specified `identification`, `password`
    and optional `scope`; issues a `POST` request to the
    {{#crossLink "OAuth2PasswordGrantAuthenticator/serverTokenEndpoint:property"}}{{/crossLink}}
    and receives the access token in response (see
    http://tools.ietf.org/html/rfc6749#section-4.3).
    __If the credentials are valid (and the optionally requested scope is
    granted) and thus authentication succeeds, a promise that resolves with the
    server's response is returned__, otherwise a promise that rejects with the
    error as returned by the server is returned.
    __If the
    [server supports it](https://tools.ietf.org/html/rfc6749#section-5.1), this
    method also schedules refresh requests for the access token before it
    expires.__
    @method authenticate
    @param {String} data The grant request object, it has to contain grant_type and any needed data
    @param {String|Array} scope The scope of the access request (see [RFC 6749, section 3.3](http://tools.ietf.org/html/rfc6749#section-3.3))
    @return {Ember.RSVP.Promise} A promise that when it resolves results in the session becoming authenticated
    @public
  */
  authenticate(data, scope = []) {
    return new RSVP.Promise((resolve, reject) => {
      const serverTokenEndpoint = this.get('serverTokenEndpoint');
      const scopesString = Ember.makeArray(scope).join(' ');
      if (!Ember.isEmpty(scopesString)) {
        data.scope = scopesString;
      }
      this.makeRequest(serverTokenEndpoint, data).then((response) => {
        run(() => {
          const expiresAt = this._absolutizeExpirationTime(response['expires_in']);
          this._scheduleAccessTokenRefresh(response['expires_in'], expiresAt, response['refresh_token']);
          if (!isEmpty(expiresAt)) {
            response = assign(response, { 'expires_at': expiresAt });
          }
          resolve(response);
        });
      }, (xhr) => {
        run(null, reject, xhr.responseJSON || xhr.responseText);
      });
    });
  }
});
