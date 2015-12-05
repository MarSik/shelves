import Ember from 'ember';

export default Ember.Helper.extend({
  compute(params, hash) {
    if (!params) {
      return "";
    }
    return Math.round(params * 100) / 100;
  }
});
