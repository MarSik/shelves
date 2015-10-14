import Ember from 'ember';

export default Ember.Controller.extend({
  actions: {
    showCreateGroup: function (group) {
      this.set('selectedGroup', group);
      $("#createGroup").foundation("reveal", "open");
    },
    showCreateTopLevelGroup: function () {
      this.set('selectedGroup', null);
      $("#createGroup").foundation("reveal", "open");
    }
  },
  topLevel: Ember.computed.filterBy('model', 'hasParent', false)
});
