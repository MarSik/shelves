import DS from 'ember-data';
import Ember from 'ember';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  name: attr(),
  summary: attr(),
  description: attr(),
  lots: hasMany("lot", {async: true}),
  parent: belongsTo("box", {async: true, inverse: "boxes"}),
  boxes: hasMany("box", {async: true, inverse: "parent"}),
  code: belongsTo("code", {async: true}),
  belongsTo: belongsTo("user", {async: true}),

  children: function() {
      return this.get('boxes');
  }.property('boxes'),
  hasChildren: function() {
      return this.get('boxes.length') != 0;
  }.property('boxes'),
  hasParent: function() {
      // XXX this is a hack, belongsTo returns a promise..
      return !Ember.isNone(this.get('parent.content'));
  }.property('parent.content'),
  fullName: function() {
      if (Ember.isNone(this.get('parent.content'))) {
          return this.get('name');
      } else {
          return this.get('parent.content.fullName') + ' | ' + this.get('name');
      }
  }.property('parent.content', 'parent.content.fullName', 'name'),

  count: function () {
    var sum = 0;
    this.get('lots').filterBy('next.length', 0).forEach(function (item, index) {
      sum += item.get('count');
    });
    return sum;
  }.property('lots.@each.count', 'lots.@each.next.length')
});
