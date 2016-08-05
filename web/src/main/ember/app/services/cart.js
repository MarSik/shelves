import Ember from 'ember';

export default Ember.Service.extend({
  store: Ember.inject.service(),

  getAll() {
    return this.get('store').findAll('cartitem');
  },

  add(lot) {
    console.log("Added item to cart");
    this.get('store').createRecord('cartitem', {
      object: lot
    }).save();
  },
  clear() {
     this.get('store').findAll('cartitem').then(function (arr) {
       arr.forEach(function (st) {
         st.destroyRecord();
       })
     });
  },
  move(destination) {
    this.get('store').findAll('cartitem').then(function (arr) {
      arr.forEach(function (lot) {
        lot.get('object').set('location', destination);
        lot.get('object').save();
      })
    });
  },
  remove(item) {
    item.destroyRecord();
  }
});
