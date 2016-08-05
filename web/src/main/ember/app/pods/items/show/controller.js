import Ember from 'ember';

export default Ember.Controller.extend({
    needs: "application",

  actions: {
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
          submitDisabled: function() {
              return this.get('requiredType') == null || this.get('requiredCount') == null || this.get('requiredCount') < 1;
          }.property('requiredType', 'requiredCount'),

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
      }.property('importableDocuments.@each')
});
