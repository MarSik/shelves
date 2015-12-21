import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        createFootprint: function () {
            this.transitionTo('footprints.new');
        }
    },
    activate: function() {
        $(document).attr('title', 'shelves - Footprints');
    },
    model: function () {
        return this.store.findAll('footprint');
    },
    afterModel() {
      this.store.findAll('footprinttype');
    }
});
