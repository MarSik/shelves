import OAuthCustomAuthenticator from '../authenticators/oauth2-w-auth';

export default {
    name:   'oauth2-w-auth',
    before: ['simple-auth', 'simple-auth-oauth2'],

    initialize: function(container) {
        container.register(
            'oauth2-w-auth:oauth2-password-grant',
            OAuthCustomAuthenticator);
    }
};
