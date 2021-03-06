import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        removeAuthorization: function(auth) {
            auth.destroyRecord();
        }
    },
    model: function() {
        return this.store.findRecord('user', 'whoami');
    },
    activate: function() {
        $(document).attr('title', 'shelves - User information');
    }
});
