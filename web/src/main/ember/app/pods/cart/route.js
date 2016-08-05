import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    cart: Ember.inject.service(),
    actions: {
        clearCart: function () {
            this.get('cart').clear();
        },
        removeFromCart: function (obj) {
            this.get('cart').remove(obj);
        }
    },
    model: function () {
        return this.get('cart').getAll();
    },
    activate: function() {
        $(document).attr('title', 'shelves - Cart');
    }
});
