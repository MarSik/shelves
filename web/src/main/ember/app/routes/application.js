import Ember from 'ember';
import ApplicationRouteMixin from 'simple-auth/mixins/application-route-mixin';

export default Ember.Route.extend(ApplicationRouteMixin, {
    actions: {
        search: function(query) {
            var search = this.store.createRecord('search', {
                query: query
            });
            var self = this;
            search.save().then(function (result) {
                self.transitionTo('search.result', result);
            });
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

            var self = this;

            newDoc.save().then(function (e) {
                self.growl.info('Document saved, downloading..');

                // Save the document to the entity
                // workarounds Many-To-Many polymorphic issue
                entity.get('describedBy').pushObject(e);
                entity.save();
            }).catch(function (e) {
                newDoc.rollback();
                self.growl.error('Download request failed: '+e);
            });
        },
        uploadFinished: function (response) {
            this.growl.info('Files uploaded');
            this.get('store').pushPayload('document', response);
        },
        deleteDocument: function (document) {
            document.destroyRecord();
        },
        deleteEntity: function (entity) {
            entity.destroyRecord();
        },
        moveLot: function (lot, destination, count) {
            var newLot = this.store.createRecord('lot', {
                count: count,
                previous: lot,
                location: destination
            });

            newLot.save().catch(function () {
                newLot.rollback();
                newLot.destroyRecord();
            });
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
            controller.set('availableFootprintTypes', self.store.filter("footprinttype", {}, function (i) {
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
            controller.set('availableSiPrefixes', self.store.filter('siprefix', {}, function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableUnits', self.store.filter('unit', {}, function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableProperties', self.store.filter('property', {}, function (i) {
                return !i.get('isNew');
            }));
            controller.set('stickers', []);

            self.store.createRecord('page', {
                name: "A4",
                custom: true,
                pageSize: 'A4',
                topMarginMm: 0,
                leftMarginMm: 0,
                stickerHorizontalCount: 1,
                stickerVerticalCount: 1,
                stickerWidthMm: 0,
                stickerHeightMm: 0,
                stickerTopMarginMm: 0,
                stickerLeftMarginMm: 0,
                rightSpaceMm: 0,
                bottomSpaceMm: 0,
                titleFontSize: 12,
                detailsFontSize: 10
            });


            self.store.createRecord('page', {
                name: "A4 Landscape",
                custom: true,
                pageSize: 'A4R',
                topMarginMm: 0,
                leftMarginMm: 0,
                stickerHorizontalCount: 1,
                stickerVerticalCount: 1,
                stickerWidthMm: 0,
                stickerHeightMm: 0,
                stickerTopMarginMm: 0,
                stickerLeftMarginMm: 0,
                rightSpaceMm: 0,
                bottomSpaceMm: 0,
                titleFontSize: 12,
                detailsFontSize: 10
            });

            self.store.createRecord('page', {
                name: "Letter",
                custom: true,
                pageSize: 'LETTER',
                topMarginMm: 0,
                leftMarginMm: 0,
                stickerHorizontalCount: 1,
                stickerVerticalCount: 1,
                stickerWidthMm: 0,
                stickerHeightMm: 0,
                stickerTopMarginMm: 0,
                stickerLeftMarginMm: 0,
                rightSpaceMm: 0,
                bottomSpaceMm: 0,
                titleFontSize: 12,
                detailsFontSize: 10
            });


            self.store.createRecord('page', {
                name: "Letter Landscape",
                custom: true,
                pageSize: 'LETTERR',
                topMarginMm: 0,
                leftMarginMm: 0,
                stickerHorizontalCount: 1,
                stickerVerticalCount: 1,
                stickerWidthMm: 0,
                stickerHeightMm: 0,
                stickerTopMarginMm: 0,
                stickerLeftMarginMm: 0,
                rightSpaceMm: 0,
                bottomSpaceMm: 0,
                titleFontSize: 12,
                detailsFontSize: 10
            });

            self.store.createRecord('page', {
                name: "Print Etikety KZK 2500 - 3x8 70x36mm",
                custom: false,
                pageSize: 'A4',
                topMarginMm: 5,
                leftMarginMm: 1,
                stickerHorizontalCount: 3,
                stickerVerticalCount: 8,
                stickerWidthMm: 70,
                stickerHeightMm: 36,
                stickerTopMarginMm: 1,
                stickerLeftMarginMm: 1,
                rightSpaceMm: 0,
                bottomSpaceMm: 0,
                titleFontSize: 12,
                detailsFontSize: 10
            });

            controller.set('availablePapers', self.store.all('page'));
        }
    }
});
