import ENV from 'webapp/config/environment';
import Ember from 'ember';

export function initialize(application) {
  if (!Ember.isEmpty(window.SHELVES_API_SERVER)) {
    ENV.APP.API_SERVER = window.SHELVES_API_SERVER;
    ENV.APP.API_BASE = window.SHELVES_API_BASE;
    ENV.APP.API_NAMESPACE = ENV.APP.API_BASE + window.SHELVES_API_NAMESPACE;
    ENV.APP.API_ENDPOINT = ENV.APP.API_SERVER + (ENV.APP.API_NAMESPACE ? '/' + ENV.APP.API_NAMESPACE : '');
  }
};

export default {
    name: 'api-server-setup',
    before: 'ember-data',
    initialize: initialize
};
