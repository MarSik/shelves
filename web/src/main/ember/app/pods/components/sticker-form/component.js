import Ember from 'ember';
import ENV from '../../../config/environment';

export default Ember.Component.extend({
    tagName: "form",
    attributeBindings: ['action'],
    session: Ember.inject.service('session'),
    action: function () {
        return ENV.APP.API_ENDPOINT + '/stickers';
    }.property(),
    access_token: function () {
        return this.get('session.data.authenticated.access_token');
    }.property('session.content')
    // items - array of items to generate the stickers for
});
