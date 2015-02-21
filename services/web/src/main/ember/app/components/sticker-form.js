import Ember from 'ember';
import ENV from '../config/environment';

export default Ember.Component.extend({
    tagName: "form",
    attributeBindings: ['action'],
    action: function () {
        return ENV.APP.API_ENDPOINT + '/stickers';
    }.property(),
    access_token: function () {
        return this.get('controller.session.content.access_token');
    }.property('controller.session.content')
    // items - array of items to generate the stickers for
});