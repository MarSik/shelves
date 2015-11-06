import Ember from 'ember';

export default Ember.Helper.extend({
  compute(params, hash) {
    console.log('SKU ', params, hash);
    return params[0].replace('{}', hash['sku']);
  }
});
