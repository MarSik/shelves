import Ember from 'ember';
import ENV from '../config/environment';

export default Ember.View.extend({
    tagName: "a",
    attributeBindings: ['href'],
    href: function () {
        var token = this.get('controller').session.content.access_token;
        var url = ENV.APP.API_ENDPOINT + '/' + this.get('models') + '/' + this.get('entity.id') + '/' + this.get('resource') + '?access_token=' + token;
        return url;
    }.property('models', 'entity.id', 'resource')
    // models
    // entity
    // resource
});