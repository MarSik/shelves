import ENV from 'webapp/config/environment';
import Ember from 'ember';

export default {
    name: 'api-server-setup',
    before: ['oauth2-w-auth'],

    initialize: function() {
        if (!Ember.isEmpty(window.SHELVES_API_SERVER)) {
             ENV.APP.API_SERVER = window.SHELVES_API_SERVER;
             ENV.APP.API_BASE = window.SHELVES_API_BASE;
             ENV.APP.API_NAMESPACE = ENV.APP.API_BASE + window.SHELVES_API_NAMESPACE;

             ENV['simple-auth-oauth2'].serverTokenEndpoint = ENV.APP.API_SERVER
                 + (ENV.APP.API_BASE ? '/' + ENV.APP.API_BASE : '') + '/oauth/token';
        }
    }
};
