import DS from 'ember-data';
import Ember from 'ember';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
  lots: hasMany("lot", {async: true}),
  parent: belongsTo("box", {async: true, inverse: "boxes"}),
  boxes: hasMany("box", {async: true, inverse: "parent"}),
  code: belongsTo("code", {async: true}),

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
    this.get('lots').filterBy('valid', true).forEach(function (item, index) {
      sum += item.get('count');
    });
    return sum;
  }.property('lots.@each.count', 'lots.@each.valid')
});
