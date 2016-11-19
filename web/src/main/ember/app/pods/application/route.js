import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';
import inject from 'webapp/injectors/repositories';

export default Ember.Route.extend(ApplicationRouteMixin, {
    capabilities: inject('capabilities'),
    session: Ember.inject.service('session'),
    store: Ember.inject.service(),
    cart: Ember.inject.service(),
    actions: {
        addToCart: function(item) {
          this.get('cart').add(item);
        },
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
        error: function(error, transition) {
            //Ember.onerror(error);
            return true;
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
                url: url,
                describes: [entity]
            });

            var self = this;

            newDoc.save().then(function (e) {
                self.growl.info('Document saved, downloading..');

                // Save the document to the entity
                // workarounds Many-To-Many polymorphic issue
                entity.get('describedBy').pushObject(e);
                entity.save();
            }).catch(function (e) {
                newDoc.rollbackAttributes();
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
            var newLot = this.store.createRecord(lot.constructor.modelName, {
                count: count,
                location: destination,
                previous: lot
            });

            newLot.save().catch(function () {
                newLot.rollbackAttributes();
            });
        },
        invalidateSession() {
            this.get('session').invalidate();
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
    beforeModel() {
      // Prepare paper records
      this.get('store').unloadAll('page');
      this.get('store').createRecord('page', {
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


      this.get('store').createRecord('page', {
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

      this.get('store').createRecord('page', {
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


      this.get('store').createRecord('page', {
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

      this.get('store').createRecord('page', {
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
    },
    model() {
      return this.get('capabilities').fetch();
    },
    afterModel() {
      var authRule = this.getParameterByName('auth');
      console.log("Auth rule: ", authRule);

      if (!this.get('session.isAuthenticated')
          && !Ember.isEmpty(authRule)) {
        var sAuth = atob(authRule);
        console.log("sAuth: ", sAuth);
        var jAuth = JSON.parse(sAuth);
        var self = this;
        if (!Ember.isEmpty(jAuth)) {
          console.log("Performing injected authentication request.");
          return this.get('session').authenticate('authenticator:generic', jAuth).then(null, function (reason) {
             self.controllerFor('application').set('error', "Authentication failed: " + JSON.stringify(reason));
          });
        }
      }
    }
});
