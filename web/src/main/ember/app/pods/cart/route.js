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
        },
        addToStickers() {
            var self = this;
            this.get('cart').getAll().then(function (cart) {
              cart.forEach(function (item) {
                console.log("adding ", item, " to stickers");
                self.send('addSticker', item.get('object'));
              });
            });
        }
    },
    model: function () {
        return this.get('cart').getAll();
    },
    activate: function() {
        $(document).attr('title', 'shelves - Cart');
    }
});
