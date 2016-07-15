import Ember from 'ember';

export default Ember.ArrayController.extend({
    actions: {
      showCreateProject() {
        this.set('showCreateDialog', true);
      },
      startProject() {
        this.set('showCreateDialog', false);
        return true;
      }
    },
    sortProperties: ['type.name'],
    sortAscending: true
});
