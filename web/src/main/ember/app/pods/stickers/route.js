import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    model: function () {
        return this.store.findAll('sticker');
    },
    activate: function() {
        $(document).attr('title', 'shelves - Stickers');
    }
});
