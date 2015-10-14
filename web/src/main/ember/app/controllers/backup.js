import Ember from 'ember';

export default Ember.Controller.extend({
  actions: {
    restoreFinished: function() {
      self.growl.info("Restore finished");
    }
  }
});
