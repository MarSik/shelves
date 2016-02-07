import Ember from 'ember';

export default Ember.Helper.extend({
  compute(params, hash) {
    return Math.round(1000.0 * params[0] / params[1]) / 10;
  }
});
