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

  free: attr(),
  available: attr(),
  total: attr(),

  groups: hasMany("group", {async: true}),
  footprints: hasMany("footprint", {async: true}),
  lots: hasMany("lot", {async: true, inverse: null}),

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

  footprint: Ember.computed('footprints.@each.name', {
      set(key, value) {
          return value;
      },
      get() {
        var self = this;

        this.get('footprints').then(function (fs) {
          var names = [];
          fs.forEach(function (fp) {
            names.pushObject(fp.get('name'));
          });
          self.set('footprint', names.join(", "));
        });

        return "";
      }
    }),

    link: function() {
        return "types.show";
    }.property(),

    icon: function () {
        return "book";
    }.property()
});
