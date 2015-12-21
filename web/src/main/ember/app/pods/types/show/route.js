import Ember from 'ember';

export default Ember.Route.extend({
    afterModel() {
      this.store.findAll('footprint');
      this.store.findAll('group');
      this.store.findAll('box');
      this.store.findAll('type');
      this.store.findAll('source');
      this.store.findAll('property');
    }
});
