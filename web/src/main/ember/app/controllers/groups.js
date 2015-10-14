import Ember from 'ember';
// global $

export default Ember.Controller.extend({
    actions: {
        createGroup: function () {
            var name = this.get('name');
            var newGroup = this.store.createRecord('group', {
                name: name,
                parent: this.get('selectedGroup')
            });
            var self = this;
            $("#createGroup").foundation("reveal", "close");

            newGroup.save()
                .then(function() {
                    /*if (self.get('selectedGroup')) {
                     self.get('selectedGroup').get('groupes').pushObject(newGroup);
                     self.store.commit();
                     }*/
                    self.growl.info("Group created");
                })
                .catch(function() {
                    newGroup.rollback();
                    self.growl.error("Group creation failed");
                });
        },
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
