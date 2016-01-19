import ENV from 'webapp/config/environment';

export default Ember.Helper.extend({
  compute(params, hash) {
    return ENV.APP.API_SERVER + (ENV.APP.API_BASE ? '/' + ENV.APP.API_BASE : '') + params[0];
  }
});
