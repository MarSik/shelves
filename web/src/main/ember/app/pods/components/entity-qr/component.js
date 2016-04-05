import Ember from 'ember';
import ENV from '../../../config/environment';

export default Ember.Component.extend({
    tagName: 'img',
    attributeBindings: ['src', 'width', 'height'],
    session: Ember.inject.service('session'),
    src: function() {
        var token = this.get('session.content.secure.access_token');
        var url = ENV.APP.API_ENDPOINT + '/' + this.get('models') + '/' + this.get('entity.id') + '/qr?access_token=' + token;
        return url;
    }.property('models', 'entity'),
    width: function() {
        return this.get('size');
    }.property('size'),
    height: function() {
        return this.get('size');
    }.property('size', 'entity.id')
});
