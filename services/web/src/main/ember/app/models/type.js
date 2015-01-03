import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  name: attr("string"),
  description: attr("string"),
  vendor: attr("string"),
  groups: hasMany("group", {async: true}),
  footprint: belongsTo("footprint", {async: true}),
  lots: hasMany("lot", {async: true, inverse: null}),
  belongsTo: belongsTo("user", {async: true}),
  describedBy: hasMany("document"),

  fullName: function () {
      return this.get('name') + ' | ' + this.get('footprint.name') + ' | ' + this.get('vendor');
  }.property('name', 'footprint.name', 'vendor'),

  available: function () {
      var sum = 0;
      this.get('lots').filterBy('next.length', 0).forEach(function (item, index) {
          sum += item.get('count');
      });
      return sum;
  }.property('lots.@each.count', 'lots.@each.next.length')
});
