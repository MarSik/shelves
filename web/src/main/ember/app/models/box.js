import DS from 'ember-data';
import Ember from 'ember';
import NamedBase from './namedbase';

var hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
  lots: hasMany("lot", {async: true, polymorphic: true, inverse: "location"}),
  validLots: Ember.computed.filterBy("lots", "valid", true),

  parent: belongsTo("box", {async: true, inverse: "boxes"}),
  boxes: hasMany("box", {async: true, inverse: "parent"}),
  code: belongsTo("code", {async: true}),

  children: function() {
      return this.get('boxes');
  }.property('boxes'),

  hasChildren: Ember.computed('boxes', 'boxes.@each', {
      set(key, value) {
          return value;
      },
      get() {
        var self = this;
        this.get('boxes').then(function (gs) {
          self.set('hasChildren', gs.get('length') !== 0);
        });
        return false;
      }
  }),

  hasParent: Ember.computed('parent', {
      set(key, value) {
          return value;
      },
      get() {
        var self = this;
        this.get('parent').then(function (p) {
          self.set('hasParent', !Ember.isNone(p));
        });

        return false;
      }
  }),

  fullName: Ember.computed('parent', 'parent.fullName', 'name', {
      set(key, value) {
          return value;
      },
      get() {
          var self = this;

          this.get('parent').then(function (p) {
              if (!Ember.isNone(p)) {
                  self.set('fullName', p.get('fullName') + ' | ' + self.get('name'));
              }
          });

          return this.get('name');
      }
  }),

  count: Ember.computed('_lots.@each.count', function () {
    let sum = 0;
    this.get('_lots').forEach(function (item) {
      sum += item.get('count');
    });
  }),

  _lots: Ember.computed('lots.@each.count', 'lots.@each.valid', function () {
    const promise = this.get('lots').then(lots => {
      return lots.filterBy('valid');
    });

    return DS.PromiseArray.create({
      promise: promise
    });
  }),

  link: function() {
      return "boxes.show";
  }.property(),

  icon: function () {
      return "archive";
  }.property()
});
