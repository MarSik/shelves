/* jshint node: true */

module.exports = function(environment) {
  var ENV = {
    modulePrefix: 'webapp',
    environment: environment,
    baseURL: '/',
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

  ENV['simple-auth'] = {
    authorizer: 'simple-auth-authorizer:oauth2-bearer',
    store: 'simple-auth-session-store:cookie',
    'client-id': 'elshelves.js',
    'client-secret': 'public',
    authenticationRoute: 'index'
  };

  if (environment === 'development') {
    // ENV.APP.LOG_RESOLVER = true;
    ENV.APP.LOG_ACTIVE_GENERATION = true;
    ENV.APP.LOG_TRANSITIONS = true;
    ENV.APP.LOG_TRANSITIONS_INTERNAL = true;
    ENV.APP.LOG_VIEW_LOOKUPS = true;

    ENV.APP.API_SERVER = 'http://localhost:8080';
    ENV.APP.API_NAMESPACE = 'api/1';

    ENV['simple-auth-oauth2'] = {
      serverTokenEndpoint: ENV.APP.API_SERVER + '/' + ENV.APP.API_NAMESPACE + '/oauth/token',
      clientId: 'elshelves.js'
    }
  }

  if (environment === 'test') {
    // Testem prefers this...
    ENV.baseURL = '/';
    ENV.locationType = 'none';

    // keep test console output quieter
    ENV.APP.LOG_ACTIVE_GENERATION = false;
    ENV.APP.LOG_VIEW_LOOKUPS = false;

    ENV.APP.rootElement = '#ember-testing';
  }

  if (environment === 'production') {
      ENV.APP.API_SERVER = 'https://api.shelves.cz';

      ENV['simple-auth-oauth2'] = {
          serverTokenEndpoint: ENV.APP.API_SERVER + '/oauth/token',
          clientId: 'elshelves.js'
      }
  }

  ENV.APP.API_ENDPOINT = ENV.APP.API_SERVER + (ENV.APP.API_NAMESPACE ? '/' + ENV.APP.API_NAMESPACE : '');
  ENV['simple-auth'].crossOriginWhitelist = [ENV.APP.API_SERVER];

  return ENV;
};
