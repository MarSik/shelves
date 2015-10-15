import Ember from 'ember';
import ENV from '../config/environment';

export default Ember.Component.extend({
    tagName: "a",
    attributeBindings: ['href'],
    href: function () {
        var token = this.get('controller.session.content.secure.access_token');
        return ENV.APP.API_ENDPOINT + '/backup?access_token=' + token;
    }.property('controller.session.content')
});
