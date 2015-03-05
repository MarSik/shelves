import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        createFootprint: function () {
            this.transitionTo('footprints.new');
        }
    },
    activate: function() {
        $(document).attr('title', 'shelves - Footprints');
    }
});
