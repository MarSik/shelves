import Ember from 'ember';
import ENV from 'webapp/config/environment';
import DS from 'ember-data';

export default Ember.Object.extend({
  fetch: function(query, type, token) {
    var link = ENV.APP.API_SERVER + (ENV.APP.API_BASE ? '/' + ENV.APP.API_BASE : '')
               + '/v1/searches';
    const self = this;
    return DS.PromiseArray.create({
      promise: Ember.$.ajax({
        dataType: "json",
        data: {
          q: query,
          t: type,
          c: 100
        },
        url: link,
        type: 'GET',
        beforeSend: xhr => xhr.setRequestHeader('Authorization', 'Bearer ' + token)
      })
    });
  }
});
