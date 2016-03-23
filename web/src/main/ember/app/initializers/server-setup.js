import ENV from 'webapp/config/environment';

export default {
    name: 'api-server-setup',
    initialize: function() {
        if (window.SHELVES_API_SERVER) ENV.APP.API_SERVER = window.SHELVES_API_SERVER;
        if (window.SHELVES_API_BASE) ENV.APP.API_BASE = window.SHELVES_API_BASE;
        if (window.SHELVES_API_NAMESPACE) ENV.APP.API_NAMESPACE = ENV.APP.API_BASE + window.SHELVES_API_NAMESPACE;
    }
};
