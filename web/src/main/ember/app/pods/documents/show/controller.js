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
    everything: function() {
      return this.store.filter("type", function (i) {
        return !i.get('isNew');
      });
    }.property()
});
