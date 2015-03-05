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

  hasChildren: function(key, value) {
      if (arguments.length > 1) {
          return value;
      }

      var self = this;
      this.get('boxes').then(function (gs) {
          self.set('hasChildren', gs.get('length') != 0);
      });
      return false;
  }.property('boxes', 'boxes.@each'),

  hasParent: function(key, value) {
      if (arguments.length > 1) {
          return value;
      }

      var self = this;
      this.get('parent').then(function (p) {
         self.set('hasParent', !Ember.isNone(p));
      });

      return false;
  }.property('parent'),

  fullName: function(key, value) {
      if (arguments.length > 1) {
          return value;
      }

      this.get('parent').then(function (p) {
          if (!Ember.isNone(p)) {
              self.set('fullName', p.get('fullName') + ' | ' + self.get('name'));
          }
      });

      return this.get('name');
  }.property('parent', 'parent.fullName', 'name'),

  count: function () {
    var sum = 0;
    this.get('lots').filterBy('valid', true).forEach(function (item, index) {
      sum += item.get('count');
    });
    return sum;
  }.property('lots', 'lots.@each.count', 'lots.@each.valid'),

  link: function() {
      return "boxes.show";
  }.property(),

  icon: function () {
      return "cube";
  }.property()
});
