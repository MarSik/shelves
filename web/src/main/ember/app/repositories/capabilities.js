import Ember from 'ember';
import ENV from 'webapp/config/environment';

export default Ember.Object.extend({
  fetch: function() {
    var link = ENV.APP.API_SERVER + (ENV.APP.API_BASE ? '/' + ENV.APP.API_BASE : '') + '/info/capabilities';
    return Ember.$.getJSON(link).then(function (d) {
      return Ember.Object.create(d);
    });
  }
});
