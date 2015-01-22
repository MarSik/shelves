import Ember from 'ember';
import ApplicationRouteMixin from 'simple-auth/mixins/application-route-mixin';

export default Ember.Route.extend(ApplicationRouteMixin, {
    actions: {
        search: function() {
            var self = this;
        },
        sessionAuthenticationSucceeded: function () {
            var controller = this.get('controller');
            this.get('preloadData')(this, controller);
            this._super();
        },
        addSticker: function (obj) {
            this.controllerFor('application').get('stickers').pushObject(obj);
        },
        download: function (entity, url) {
            var newDoc = this.store.createRecord('document', {
                url: url
            });
            newDoc.get('describes').pushObject(entity);

            var self = this;

            newDoc.save().then(function (e) {
                self.growl.info('Document saved, downloading..');
            }).catch(function (e) {
                newDoc.rollback();
                self.growl.error('Download request failed: '+e);
            })
        },
        uploadFinished: function (response) {
            this.growl.info('Files uploaded');
            this.get('store').pushPayload(response);
        },
        deleteDocument: function (document) {
            document.destroyRecord();
        }
    },
    setupController: function(controller, model) {
        controller.set('model', model);
        this.get('preloadData')(this, controller);
    },
    preloadData: function (self, controller) {
        if (self.get('session.isAuthenticated')) {
            controller.set('availableFootprints', self.store.filter("footprint", {}, function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableGroups', self.store.filter('group', {}, function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableTypes', self.store.filter('type', {}, function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableLocations', self.store.filter('box', {}, function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableSources', self.store.filter('source', {}, function (i) {
                return !i.get('isNew');
            }));
        }
    }
});
