import Ember from 'ember';

export default Ember.Controller.extend({
  codeTypes: [
    {id: "UPC_A", name: "UPC-A"},
    {id: "UPC_E", name: "UPC-E"},
    {id: "EAN_8", name: "EAN-8"},
    {id: "EAN_13", name: "EAN-13"},
    {id: "CODE_39", name: "Code 39"},
    {id: "CODE_93", name: "Code 93"},
    {id: "CODE_128", name: "Code 128"},
    {id: "CODABAR", name: "Codabar"},
    {id: "ITF", name: "ITF"},
    {id: "RSS_14", name: "RSS-14"},
    {id: "RSS_EXPANDED", name: "RSS-Expanded"},
    {id: "QR_CODE", name: "QR Code"},
    {id: "DATA_MATRIX", name: "Data Matrix"},
    {id: "AZTEC", name: "Aztec"},
    {id: "PDF_417", name: "PDF 417"},
    {id: "QR+SHV", name: "Shelves UUID alias"}
  ],

  showCodeForModelPlural: Ember.computed("showCodeFor", function () {
    return Ember.Inflector.inflector.pluralize(this.get('showCodeFor.constructor.modelName'));
  }),

  session: Ember.inject.service('session'),
  cart: Ember.inject.service(),
  store: Ember.inject.service(),

  availableFootprints: Ember.computed(function() {
    return this.get('store').findAll("footprint");
  }),
  availableFootprintTypes: Ember.computed(function() {
    return this.get('store').findAll("footprinttype");
  }),
  availableGroups: Ember.computed(function() {
    return this.get('store').findAll('group'); 
  }),
  availableTypes: Ember.computed(function() {
    return this.get('store').findAll('type'); 
  }),
  availableLocations: Ember.computed(function() {
    return this.get('store').findAll('box');
  }),
  availableSources: Ember.computed(function() {
    return this.get('store').findAll('source');
  }),
  availableSiPrefixes: Ember.computed(function() {
    return this.get('store').findAll('siprefix');
  }),
  availableUnits: Ember.computed(function() {
    return this.get('store').findAll('unit');
  }),
  availableProperties: Ember.computed(function() {
    return this.get('store').findAll('property');
  }),
  availablePapers: Ember.computed(function() {
    return this.get('store').peekAll('page');
  }),
  stickers: Ember.computed(function() {
    return this.get('store').findAll('sticker');
  }),
  cartitems: Ember.computed(function() {
    return this.get('cart').getAll();
  })
});
