import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  name: attr("string"),
  summary: attr("string"),
  description: attr("string"),
  vendor: attr("string"),
  groups: hasMany("group", {async: true}),
  footprint: belongsTo("footprint", {async: true}),
  lots: hasMany("lot", {async: true, inverse: null}),
  belongsTo: belongsTo("user", {async: true}),
  describedBy: hasMany("document", {async: true}),

  fullName: function () {
      var n = this.get('name') + ' | ' + this.get('footprint.name');
      if (this.get('vendor')) {
          n += ' | ' + this.get('vendor');
      }
      if (this.get('vendor')) {
          n += ' | ' + this.get('summary');
      }

      n += " (" + this.get('available') + ")";

      return n;
  }.property('name', 'footprint.name', 'vendor', 'summary', 'available'),

  available: function () {
      var sum = 0;
      this.get('lots').filterBy('next.length', 0).forEach(function (item, index) {
          sum += item.get('count');
      });
      return sum;
  }.property('lots.@each.count', 'lots.@each.next.length')
});
