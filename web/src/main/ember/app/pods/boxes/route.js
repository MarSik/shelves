import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        selectBox: function (box) {
            this.transitionTo('boxes.show', box);
        },
        showQr: function (models, entity) {
            this.set('controller.qrEntity', entity);
            this.set('controller.qrModels', models);
        },
        hideQr: function () {
            this.set('controller.qrEntity', null);
            this.set('controller.qrModels', null);
        },
        moveItem: function (itemId, boxId) {
            var self = this;
            console.log('drop processing');
            this.store.findRecord('lot', itemId).then(function (item) {
                self.store.findRecord('box', boxId).then(function (box) {
                    self.send('moveLot', item, box);
                });
            });
        }
    },
    activate: function() {
        $(document).attr('title', 'shelves - Locations');
    },
    model: function () {
        return Ember.A();
    }
});
