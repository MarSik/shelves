import Ember from 'ember';
import ENV from '../../config/environment';

export default Ember.Controller.extend({
    tokenQrUrl: function () {
        var token = this.get('session.content.secure.access_token');
        var apiBase = ENV.APP.API_SERVER + (ENV.APP.API_BASE ? '/' + ENV.APP.API_BASE : '');
        return apiBase.replace(/^(.*?):\/\//, "shv+token+$1://") + "/" + token;
    }.property('session.content.secure.access_token')
});
