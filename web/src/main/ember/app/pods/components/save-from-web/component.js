import Ember from 'ember';

export default Ember.Component.extend({
    actions: {
        download: function () {
            this.get('action')(this.get('entity'), this.get('url'));
            this.set('url', '');
        }
    },
    disabled: function() {
        return Ember.isEmpty(this.get('url'));
    }.property('url'),
    url: ""
    // entity
});
