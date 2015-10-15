import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';
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
            this.store.find('lot', itemId).then(function (item) {
                self.store.find('box', boxId).then(function (box) {
                    self.send('moveLot', item, box);
                });
            });
        }
    },
    activate: function() {
        $(document).attr('title', 'shelves - Locations');
    },
    model: function () {
        return this.store.peekAll('box');
    }
});
