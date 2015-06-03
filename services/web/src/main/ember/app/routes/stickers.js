import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    model: function () {
        return this.controllerFor('application').get('stickers');
    },
    activate: function() {
        $(document).attr('title', 'shelves - Stickers');
    }
});
