import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
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
                unit.rollbackAttributes();
            });
        }
    },
    model: function () {
        return this.store.peekAll('unit').filterBy('isNew', false);
    },
    activate: function() {
        $(document).attr('title', 'shelves - Measurement and property units');
    }
});
