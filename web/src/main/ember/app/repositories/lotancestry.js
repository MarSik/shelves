import Ember from 'ember';
import ENV from 'webapp/config/environment';
import DS from 'ember-data';

export default Ember.Object.extend({
  fetch: function(lotId, token) {
    var link = ENV.APP.API_SERVER + (ENV.APP.API_BASE ? '/' + ENV.APP.API_BASE : '')
               + '/v1/lots/'
               + lotId + '/ancestry';
    return DS.PromiseArray.create({
      promise: Ember.$.ajax({
        dataType: "json",
        url: link,
        type: 'GET',
        success: d => d.map(it => Ember.Object.create(it)),
        beforeSend: xhr => xhr.setRequestHeader('Authorization', 'Bearer ' + token)
      })
    });
  }
});


