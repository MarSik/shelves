import Ember from 'ember';

export default Ember.Helper.extend({
  compute(params, hash) {
    console.log("PR "+params);
    return Math.round(params * 100) / 100;
  }
});
