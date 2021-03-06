import Ember from 'ember';
import ENV from '../../../config/environment';

export default Ember.Component.extend({
    tagName: 'img',
    attributeBindings: ['src', 'width', 'height'],
    session: Ember.inject.service('session'),
    src: function() {
        var token = this.get('session.data.authenticated.access_token');
        var url = ENV.APP.API_ENDPOINT + '/' + this.get('models') + '/' + this.get('entity.id') + '/' + this.get('resource') + '?size=' + this.get('size') + '&access_token=' + token;
        return url;
    }.property('models', 'entity', 'resource'),
    width: function() {
        return this.get('size');
    }.property('size'),
    height: function() {
        return this.get('size');
    }.property('size', 'entity.id')
});
