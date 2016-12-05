import Ember from 'ember';
import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany;

export default NamedBase.extend({
  vendor: attr("string"),
  serials: attr("boolean"),
  manufacturable: attr("boolean"),
  customId: attr('string'),

  minimumCount: attr('number'),
  buyMultiple: attr('number'),

  skus: attr(),
  skuValues: attr('object'),

  free: attr(),
  available: attr(),
  total: attr(),

  groups: hasMany("group", {async: true}),
  footprints: hasMany("footprint", {async: true}),
  lots: hasMany("lot", {async: true, inverse: "type", polymorphic: true}),
  validLots: Ember.computed.filterBy("lots", "valid", true),
  assignableLots: Ember.computed.filterBy("validLots", "canBeAssigned", true),

  seeAlso: hasMany("type", {async: true, inverse: "seeAlso"}),

  fullName: function () {
      var n = this.get('name') + ' | ' + this.get('footprint');
      if (this.get('vendor')) {
          n += ' | ' + this.get('vendor');
      }
      if (this.get('summary')) {
          n += ' | ' + this.get('summary');
      }

      n += " (" + this.get('free') + " / " + this.get('available') + ")";

      return n;
  }.property('name', 'footprint', 'vendor', 'summary', 'available', 'free'),

  _footprints: Ember.computed('footprints.@each', {
    const promise = this.get('footprints').then(footprints => {
      return footprints;
    });

    return DS.PromiseArray.create({
      promise: promise
    });
  }),

  footprint: Ember.computed('_footprints.@each.name', function () {
    var names = [];
    this.get("_footprints").forEach(function (fp) {
      names.pushObject(fp.get('name'));
    });
    return names.join(", ");
  }),

  link: function() {
      return "types.show";
  }.property(),

  icon: function () {
      return "book";
  }.property()
});
