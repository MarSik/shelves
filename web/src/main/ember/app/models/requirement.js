import Ember from 'ember';
import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
  version: attr(),
  name: attr('string'),
    summary: attr('string'),
    count: attr('number'),
    type: hasMany('type', {inverse: null, async: true}),
    item: belongsTo('item', {async: true, inverse: 'requirements'}),
    lots: hasMany('lot', {async: true, polymorphic: true}),

    typeCanBeRemoved: function () {
        return this.get('type.length') > 1;
    }.property('type'),

    missing: Ember.computed('lots.@each.count', 'count', {
      set(key, value) {
          return value;
      },
      get() {
        var self = this;
        this.get('lots').then(function (lots) {
          var assigned = 0;
          lots.forEach(function (lot) {
            assigned += lot.get('count');
          });
          self.set('missing', self.get('count') - assigned);
        });
        return 0;
      }
    }),

    progress: function () {
        var sum = this.get('count');
        var soldered = 0;
        var assigned = 0;

        this.get('lots').forEach(function (req) {
            var prog = req.get('progress');
            soldered += prog[0];
            assigned += prog[1];
        });

        return [soldered, sum];

    }.property('count', 'lots.@each.progress')
});
