import Ember from 'ember';
import ENV from '../config/environment';

export default Ember.Controller.extend({
    tokenQrUrl: function () {
        var token = this.get('session.content.secure.access_token');
        return ENV.APP.API_ENDPOINT.replace(/^(.*?):\/\//, "shv+token+$1://") + "/" + token;
    }.property('session.content.secure.access_token')
});
