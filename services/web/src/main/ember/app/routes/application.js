import Ember from 'ember';
import ApplicationRouteMixin from 'simple-auth/mixins/application-route-mixin';

export default Ember.Route.extend(ApplicationRouteMixin, {
    actions: {
        search: function() {
            var self = this;
        }
    },
    setupController: function(controller, model) {
        controller.set('model', model);
        controller.set('availableFootprints', this.store.filter("footprint", {}, function (i) {
            return !i.get('isNew');
        }));
        controller.set('availableGroups', this.store.filter('group', {}, function (i) {
            return !i.get('isNew');
        }));
        controller.set('availableTypes', this.store.filter('type', {}, function (i) {
            return !i.get('isNew');
        }));
        controller.set('availableLocations', this.store.filter('box', {}, function (i) {
            return !i.get('isNew');
        }));
        controller.set('availableSources', this.store.filter('source', {}, function (i) {
            return !i.get('isNew');
        }));
    }
});
