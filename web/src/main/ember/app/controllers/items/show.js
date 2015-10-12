import Ember from 'ember';
import ENV from '../../config/environment';
/* global $ */

export default Ember.Controller.extend({
    needs: "application",
    actions: {
        showAddAlternative: function (req) {
            this.set('lastRequirement', req);
            this.set('showAlternativeDialog', true);
        },
        addAlternativePart: function () {
            this.set('showAlternativeDialog', false);
            return true;
        },
        assignLot: function (req, lots) {
            this.set('assignLotToRequirement', req);
            this.set('assignableLots', lots);
        },
        performAssignment: function (req, lot, count) {
            lot.set('status', 'ASSIGNED');
            lot.set('usedBy', req);
            lot.set('count', count);

            var self = this;

            lot.save().catch(function (e) {
                lot.rollback();
                self.growl.error(e);
            }).then(function () {
                console.log("MISSING");
                console.log(req.get('missing'));
                if (req.get('missing') <= 0) {
                    self.set('assignLotToRequirement', null);
                    self.set('assignableLots', []);
                }
            });
        },
        closeAssignment: function () {
            this.set('assignLotToRequirement', null);
            this.set('assignableLots', []);
        },
        unassignLot: function (lot) {
            lot.set('status', 'UNASSIGNED');
            lot.set('usedBy', null);

            var self = this;

            lot.save().catch(function (e) {
                lot.rollback();
                self.growl.error(e);
            });
        },
        solderLot: function (lot) {
          lot.set('status', 'SOLDERED');

          var self = this;

          lot.save().catch(function (e) {
            lot.rollback();
            self.growl.error(e);
          });
        },
      finish (lot) {
        lot.set('finished', true);

        var self = this;

        lot.save().catch(function (e) {
          lot.rollback();
          self.growl.error(e);
        });
      },
      reopen (lot) {
        lot.set('finished', false);

        var self = this;

        lot.save().catch(function (e) {
          lot.rollback();
          self.growl.error(e);
        });
      },
        unsolderLot: function (lot) {
          lot.set('status', 'UNSOLDERED');

          var self = this;

          lot.save().catch(function (e) {
            lot.rollback();
            self.growl.error(e);
          });
        },
        importRequirements: function (document) {
            var url = ENV.APP.API_ENDPOINT + '/projects/' + this.get('model.id') + '/import';
            var self = this;
            $.post(url, {
                document: document.get('id')
            }, function (data) {
                self.get('store').pushPayload('project', data);
            });
        },
        toggleImportRequirements: function () {
            this.set('showImportRequirements', !this.get('showImportRequirements'));
        },
        toggleAddRequirement: function () {
            this.set('showAddRequirement', !this.get('showAddRequirement'));
        }
    },
    typeSorting: ['name'],
    sortedTypes: Ember.computed.sort('controllers.application.availableTypes', 'typeSorting'),
    requiredType: null,
    requiredCount: 1,
    lastRequirement: null,
    alternativeType: null,
    submitDisabled: function() {
        return this.get('requiredType') == null || this.get('requiredCount') == null || this.get('requiredCount') < 1;
    }.property('requiredType', 'requiredCount'),

    assignLotToRequirement: null,
    assignableLots: [],

    displayRequirements: Ember.computed.map('model.requirements', m => m),
    importableDocuments: Ember.computed.filterBy('model.describedBy', 'contentType', 'application/x-kicad-schematic'),

    showAddRequirement: false,
    addRequirementClass: function () {
        if (this.get('showAddRequirement')) {
            return "primary button";
        } else {
            return "secondary button";
        }
    }.property('showAddRequirement'),

    showImportRequirements: false,
    importRequirementsClass: function () {
        if (this.get('showImportRequirements')) {
            return "primary button";
        } else {
            return "secondary button";
        }
    }.property('showImportRequirements'),

    importableDocumentPresent: function () {
        var docs = this.get('importableDocuments');
        return !Ember.isEmpty(docs);
    }.property('importableDocuments.@each'),

  _history: null,
  history: function () {
    if (this.get('_history') == null ||
      this.get('_history')[0].get('id') !== this.get('model.history.id')) {
      this.set('_history', []);
      var self = this;
      this.get('model.history').then(function (h) {
        self.set('_history', [h]);
        Ember.run.next(self, 'loadAdditionalHistory');
      });
    }

    return this.get('_history');
  }.property('_history', 'model.history.id'),

  loadAdditionalHistory: function () {
    var history = this.get('_history');
    var self = this;

    if (Ember.isEmpty(history)) {
      return;
    }

    var latest = history[ history.length - 1 ];

    latest.get('previous').then(function (p) {
      if (Ember.isEmpty(p)) {
        return;
      }

      history.pushObject(p);
      Ember.run.next(self, 'loadAdditionalHistory');
    }).catch(function (err) {
      console.log("Err "+err);
    });
  }
});
