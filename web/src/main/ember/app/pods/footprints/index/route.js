import Ember from 'ember';

export default Ember.Route.extend({
  model: function () {
    return this.store.findAll('footprint');
  },
  afterModel() {
    this.store.findAll('footprinttype');
  }
});
