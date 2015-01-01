import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    model: function() {
        return this.store.filter('group', {}, function (box) {
            // return only top level boxes
            return !box.get('hasParent');
        });
    }
});