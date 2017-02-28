import Ember from 'ember';
import ENV from '../../config/environment';

/* global $ */

export default Ember.Controller.extend({
    unused: Ember.computed('model.partCount.total', 'model.partCount.usedInPast', function () {
      var total = this.get('model.partCount.total');
      var used = this.get('model.partCount.usedInPast');

      return total - used;
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
