import Ember from 'ember';

export default Ember.Controller.extend({
  actions: {
    showCreateGroup: function (group) {
      this.set('selectedGroup', group);
      this.set('showCreateDialog', true);
    },
    showCreateTopLevelGroup: function () {
      this.set('selectedGroup', null);
      this.set('showCreateDialog', true);
    },
    createGroup: function (name) {
      var newGroup = this.store.createRecord('group', {
        name: name,
        parent: this.get('selectedGroup')
      });
      var self = this;
      this.set('showCreateDialog', false);

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
    }
  },
  topLevel: Ember.computed.filterBy('model', 'hasParent', false)
});