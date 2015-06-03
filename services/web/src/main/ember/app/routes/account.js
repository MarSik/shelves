import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        removeAuthorization: function(auth) {
            auth.destroyRecord();
        }
    },
    model: function() {
        return this.store.find('user', 'whoami');
    },
    activate: function() {
        $(document).attr('title', 'shelves - User information');
    }
});
