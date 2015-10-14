import Ember from 'ember';

export default Ember.ArrayController.extend({
    actions: {
      showCreateProject() {
        $("#createProject").foundation("reveal", "open");
      },
      startProject() {
        $("#createProject").foundation("reveal", "close");
        return true;
      }
    },
    sortProperties: ['name'],
    sortAscending: true
});
