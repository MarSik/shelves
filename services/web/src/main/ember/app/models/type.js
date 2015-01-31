import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
  vendor: attr("string"),
  serials: attr("boolean"),

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

  footprint: function () {
      var names = [];

      this.get('footprints').forEach(function (fp) {
          names.pushObject(fp.get('name'));
      });

      return names.join(", ");
  }.property('footprints.@each.name')
});
