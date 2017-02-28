import Ember from 'ember';
import ENV from 'webapp/config/environment';
import DS from 'ember-data';

export default Ember.Object.extend({
  fetch: function(token) {
    var link = ENV.APP.API_SERVER + (ENV.APP.API_BASE ? '/' + ENV.APP.API_BASE : '')
               + '/v1/users/info';
    return DS.PromiseObject.create({
      promise: Ember.$.ajax({
        dataType: "json",
        url: link,
        type: 'GET',
        success: d => Ember.Object.create(d),
        beforeSend: xhr => xhr.setRequestHeader('Authorization', 'Bearer ' + token)
      })
    });
  }
});
