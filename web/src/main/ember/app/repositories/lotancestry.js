import Ember from 'ember';
import ENV from 'webapp/config/environment';
import DS from 'ember-data';

export default Ember.Object.extend({
  fetch: function(lotId, token) {
    var link = ENV.APP.API_SERVER + (ENV.APP.API_BASE ? '/' + ENV.APP.API_BASE : '')
               + '/v1/lots/'
               + lotId + '/ancestry?access_token=' + token;
    return DS.PromiseArray.create({
      promise: Ember.$.getJSON(link).then(function (d) {
        return d.map(it => Ember.Object.create(it));
      })
    });
  }
});
