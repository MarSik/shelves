/* jshint node: true */

module.exports = function(environment) {
  var ENV = {
    modulePrefix: 'webapp',
    podModulePrefix: 'webapp/pods',
    environment: environment,
    rootURL: '/',
    locationType: 'auto',
    EmberENV: {
      FEATURES: {
        // Here you can enable experimental features on an ember canary build
        // e.g. 'with-controller': true
      }
    },

    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
    }
  };

  ENV['ember-simple-auth'] = {
    'client-id': 'elshelves.js',
    'client-secret': 'public',
    authenticationRoute: 'signin'
  };

  if (environment === 'development') {
    // ENV.APP.LOG_RESOLVER = true;
    ENV.APP.LOG_ACTIVE_GENERATION = true;
    ENV.APP.LOG_TRANSITIONS = true;
    ENV.APP.LOG_TRANSITIONS_INTERNAL = true;
    ENV.APP.LOG_VIEW_LOOKUPS = true;

    ENV.APP.API_SERVER = 'http://localhost:8080';
    ENV.APP.API_BASE = '';
    ENV.APP.API_NAMESPACE = ENV.APP.API_BASE + 'v1';
      ENV.APP.API_SERVER = 'https://api.shelves.cz';
      ENV.APP.API_BASE = '';
      ENV.APP.API_NAMESPACE = ENV.APP.API_BASE + 'v1';
  }

  if (environment === 'test') {
    // Testem prefers this...
    ENV.rootURL = '/';
    ENV.locationType = 'none';

    // keep test console output quieter
    ENV.APP.LOG_ACTIVE_GENERATION = false;
    ENV.APP.LOG_VIEW_LOOKUPS = false;

    ENV.APP.rootElement = '#ember-testing';
  }

  if (environment === 'production') {
      ENV.APP.API_SERVER = 'https://a.shelves.cz';
      ENV.APP.API_BASE = 'api';
      ENV.APP.API_NAMESPACE = ENV.APP.API_BASE + '/v1';
  }

  ENV.APP.API_ENDPOINT = ENV.APP.API_SERVER + (ENV.APP.API_NAMESPACE ? '/' + ENV.APP.API_NAMESPACE : '');

  return ENV;
};
