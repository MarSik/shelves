import Ember from 'ember';
import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
    types: hasMany("type", {async: true}),
    directCount: attr(),
    nestedCount: attr(),

    parent: belongsTo("group", {inverse: "groups", async: true}),
    groups: hasMany("group", {inverse: "parent", async: true}),
    showProperties: hasMany('property', {async: true}),

    children: function() {
        return this.get('groups');
    }.property('groups'),

    hasChildren: Ember.computed('groups', 'groups.@each', {
        set(key, value) {
            return value;
        },
        get() {
          var self = this;
          this.get('groups').then(function (gs) {
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

    count: function () {
        var c = this.get('nestedCount');
        if (this.get('hasChildren')) {
            c = this.get('directCount') + ' / ' + c;
        }
        return c;
    }.property('nestedCount', 'hasChildren', 'directCount'),

    link: function() {
        return "groups.show";
    }.property(),

    icon: function () {
        return "database";
    }.property()
});
