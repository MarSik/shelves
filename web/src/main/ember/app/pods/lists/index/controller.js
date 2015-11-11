import Ember from 'ember';

export default Ember.ArrayController.extend({
    actions: {
      showCreateList() {
        this.set('showCreateDialog', true);
      },
      createList() {
        this.set('showCreateDialog', false);
        return true;
      }
    },
    sortProperties: ['name'],
    sortAscending: true
});