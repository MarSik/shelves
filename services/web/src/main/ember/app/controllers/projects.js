import Ember from 'ember';

export default Ember.ArrayController.extend({
    projectName: "New project",
    sortProperties: ['name'],
    sortAscending: true
});
