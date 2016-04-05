import Ember from 'ember';
import ENV from '../../../config/environment';

export default Ember.Component.extend({
    tagName: "a",
    attributeBindings: ['href'],
    session: Ember.inject.service('session'),
    href: function () {
        var token = this.get('session.content.secure.access_token');
        return ENV.APP.API_ENDPOINT + '/backup?access_token=' + token;
    }.property('session.content')
});
