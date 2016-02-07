import DS from 'ember-data';
import Ember from 'ember';
import NamedBase from './namedbase';

var hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
  lots: hasMany("lot", {async: true}),
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

  count: Ember.computed('lots', 'lots.@each.count', 'lots.@each.valid', {
      set(key, value) {
          console.log(this.get('name') + ' box count ' + value);
          return value;
      },
      get() {
        var self = this;

        this.get('lots').then(function (lots) {
          var sum = 0;
          lots.filterBy('valid', true).forEach(function (item) {
            sum += item.get('count');
          });
          self.set('count', sum);
        });

        return 0;
      }
  }),

  link: function() {
      return "boxes.show";
  }.property(),

  icon: function () {
      return "archive";
  }.property()
});
