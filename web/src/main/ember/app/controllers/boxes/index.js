import Ember from 'ember';

export default Ember.Controller.extend({
  actions: {
    showCreateBox: function (box) {
      this.set('selectedBox', box);
      $("#createBox").foundation("reveal", "open");
    },
    showCreateTopLevelBox: function () {
      this.set('selectedBox', null);
      $("#createBox").foundation("reveal", "open");
    }
  },
  topLevel: Ember.computed.filterBy('model', 'hasParent', false)
});
