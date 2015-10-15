import Ember from 'ember';

export default Ember.Controller.extend({
  actions: {
    showCreateBox: function (box) {
      this.set('selectedBox', box);
      this.set('showCreateDialog', true);
    },
    showCreateTopLevelBox: function () {
      this.set('selectedBox', null);
      this.set('showCreateDialog', true);
    },
    createBox: function (name) {
      var newBox = this.store.createRecord('box', {
        name: name,
        parent: this.get('selectedBox')
      });
      var self = this;
      this.set('showCreateDialog', false);


      newBox.save()
        .then(function() {
          /*if (self.get('selectedBox')) {
           self.get('selectedBox').get('boxes').pushObject(newBox);
           self.store.commit();
           }*/
          self.growl.info("Box created");
        })
        .catch(function() {
          newBox.rollback();
          self.growl.error("Box creation failed");
        });
    }
  },
  topLevel: Ember.computed.filterBy('model', 'hasParent', false)
});
