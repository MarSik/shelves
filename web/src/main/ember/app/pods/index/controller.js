import Ember from 'ember';
import ENV from '../../config/environment';

/* global $ */

export default Ember.Controller.extend({
    unused: Ember.computed('model.partCount.total', 'model.partCount.used', 'model.partCount.usedInPast', 'model.partCount.assigned', function () {
      var total = this.get('model.partCount.total');
      var used = this.get('model.partCount.used');
      var usedInPast = this.get('model.partCount.usedInPast');
      var assigned = this.get('model.partCount.assigned');

      // assigned is also used
      // used is also usedInPast
      return total - (assigned - used) - usedInPast;
    }),

    reserved: Ember.computed('model.partCount.assigned', 'model.partCount.used', function () {
      var reserved = this.get('model.partCount.assigned');
      var used = this.get('model.partCount.used');
      console.log(reserved + "+" + used);
      return reserved - used;
    }),

    usedInPast: Ember.computed('model.partCount.used', 'model.partCount.usedInPast', function () {
      var used = this.get('model.partCount.used');
      var usedInPast = this.get('model.partCount.usedInPast');

      return usedInPast - used;
    }),

    used: Ember.computed('model.partCount.used', function () {
      var used = this.get('model.partCount.used');

      return used;
    }),
});
