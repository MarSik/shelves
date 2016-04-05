import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    model: function () {
        return this.store.find('transaction', {fullyDelivered: false});
    },
    activate: function() {
        $(document).attr('title', 'shelves - Purchase(s)');
    }
});
