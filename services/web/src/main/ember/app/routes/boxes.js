import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';

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
        }
    },
    model: function() {
        return this.store.filter('box', function (box) {
            // return only top level boxes
            return !box.get('hasParent');
        });
    },
    activate: function() {
        $(document).attr('title', 'shelves - Locations');
    }
});