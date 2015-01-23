import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    model: function () {
        return this.store.filter('property', function (fp) {
            return !fp.get('isNew');
        });
    }
});
