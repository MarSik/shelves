import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        showAddDescribes() {
            this.set('displayAddDescribes', true);
        },
        hideAddDescribes() {
            this.set('displayAddDescribes', true);
        }
    },
    displayAddDescribes: false,
    everything: Ember.computed(function() {
      return this.store.findAll("type");
    })
});
