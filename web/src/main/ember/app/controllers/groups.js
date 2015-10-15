import Ember from 'ember';
// global $

export default Ember.Controller.extend({
    actions: {
        deleteGroup: function(group) {
            group.destroyRecord();
        },
        selectGroup: function(group) {
            this.set('selectedGroup', group);
            this.transitionTo('groups.show', group);
        },
        hoverGroup: function(group) {
            this.set('selectedGroup', group);
        }
    },
    sortProperties: ['name'],
    sortAscending: true,
    topLevel: Ember.computed.filterBy('model', 'hasParent', false)
});
