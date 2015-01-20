import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
  vendor: attr("string"),

  free: attr(),
  available: attr(),
  total: attr(),

  groups: hasMany("group", {async: true}),
  footprint: belongsTo("footprint", {async: true}),
  lots: hasMany("lot", {async: true, inverse: null}),

  fullName: function () {
      var n = this.get('name') + ' | ' + this.get('footprint.name');
      if (this.get('vendor')) {
          n += ' | ' + this.get('vendor');
      }
      if (this.get('vendor')) {
          n += ' | ' + this.get('summary');
      }

      n += " (" + this.get('free') + " / " + this.get('available') + ")";

      return n;
  }.property('name', 'footprint.name', 'vendor', 'summary', 'available', 'free')
});
