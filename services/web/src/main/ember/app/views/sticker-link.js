import Ember from 'ember';
import ENV from '../config/environment';

export default Ember.View.extend({
    tagName: "a",
    attributeBindings: ['href'],
    href: function () {
        var token = this.get('controller.session.content.access_token');
        var url = ENV.APP.API_ENDPOINT + '/stickers?access_token=' + token;
        console.log(this.get('items'));
        this.get('items').forEach(function (i) {
            url += "&uuids[]=" + i.get('id');
        });
        return url;
    }.property('controller.session.content', 'items.@each.id')
    // items - array of items to generate the stickers for
});