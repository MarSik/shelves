import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        createUnit: function(name, symbol) {
            var unit = this.store.createRecord('unit', {
                name: name,
                symbol: symbol
            });

            var self = this;

            unit.save().then(function (u) {
                self.transitionTo('units.show', u);
            }).catch(function () {
                unit.rollback();
            });
        }
    },
    model: function () {
        return this.store.filter('unit', function (fp) {
            return !fp.get('isNew');
        });
    },
    activate: function() {
        $(document).attr('title', 'shelves - Measurement and property units');
    }
});
