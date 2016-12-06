import Ember from 'ember';
import DS from 'ember-data';
import IdentifiedBase from './identifiedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default IdentifiedBase.extend({
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

    _lots: Ember.computed('lots.@each.valid', function () {
      const promise = this.get('lots').then(lots => {
        return lots.filterBy('valid');
      });

      return DS.PromiseArray.create({
        promise: promise
      });
    }),

    missing: Ember.computed('_lots.@each.count', 'count', function () {
      var assigned = 0;
      this.get('_lots').forEach(function (lot) {
        assigned += lot.get('count');
      });
      return this.get('count') - assigned;
    }),

    progress: function () {
        var sum = this.get('count');
        var soldered = 0;
        var assigned = 0;

        this.get('_lots').forEach(function (req) {
            var prog = req.get('progress');
            soldered += prog[0];
            assigned += prog[1];
        });

        return [soldered, sum];

    }.property('count', '_lots.@each.progress')
});
