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
            this._super();
            this.refresh();
        },
        loading: function() {
            this.intermediateTransitionTo("loading");
        },
        addSticker: function (obj) {
            this.store.createRecord('sticker', {
              object: obj
            }).save();
        },
        addBarcode(model) {
            this.controllerFor('application').set('barcodedItem', model);
        },
        createBarcode(model, type, code) {
            var c = this.store.createRecord('code', {
              code: code,
              type: type.id,
              reference: model
            });

            var self = this;

            c.save().catch(function (e) {
              c.destroyRecord();
            }).then(function () {
              self.controllerFor('application').set('barcodedItem', undefined);
            });
        },
        showBarcode(model) {
            this.controllerFor('application').set('showCodeFor', model);
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
            lot.set('count', count);
            lot.set('location', destination);

            lot.save().catch(function () {
                lot.rollback();
            });
        }
    },
    willTransition(transition) {
      this.controllerFor('application').set('barcodedItem', undefined);
      this.controllerFor('application').set('showCodeFor', undefined);
    },
    getParameterByName(name) {
      name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
      var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
      return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    },
    model() {
      var authRule = this.getParameterByName('auth');
      console.log("Auth rule: ", authRule);

      if (!this.get('session.isAuthenticated')
          && !Ember.isEmpty(authRule)) {
        var sAuth = atob(authRule);
        console.log("sAuth: ", sAuth);
        var jAuth = JSON.parse(sAuth);
        if (!Ember.isEmpty(jAuth)) {
          console.log("Performing injected authentication request.");
          return this.get('session').authenticate('oauth2-w-auth:oauth2-password-grant', jAuth).then(null, function () {
            return "Authentication failed.";
          });
        }
      }
    },
    setupController: function(controller, model) {
        controller.set('model', model);
        return this.preloadData(this, controller);
    },
    preloadData: function (self, controller) {
        if (self.get('session.isAuthenticated')) {
            controller.set('availableFootprints', self.store.filter("footprint", function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableFootprintTypes', self.store.filter("footprinttype", {}, function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableGroups', self.store.filter('group', function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableTypes', self.store.filter('type', function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableLocations', self.store.filter('box', function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableSources', self.store.filter('source', function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableSiPrefixes', self.store.filter('siprefix', {}, function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableUnits', self.store.filter('unit', function (i) {
                return !i.get('isNew');
            }));
            controller.set('availableProperties', self.store.filter('property', function (i) {
                return !i.get('isNew');
            }));

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
                stickerLeftMarginMm: 3,
                rightSpaceMm: 0,
                bottomSpaceMm: 0,
                titleFontSize: 12,
                detailsFontSize: 10
            });

            controller.set('availablePapers', self.store.all('page'));
            var stickers = self.store.findAll('sticker');
            controller.set('stickers', stickers);
            return stickers;
        }
    }
});
