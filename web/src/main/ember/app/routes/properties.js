import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    model: function () {
        return this.store.filter('property', function (fp) {
            return !fp.get('isNew');
        });
    },
    activate: function() {
        $(document).attr('title', 'shelves - Properties');
    }
});
