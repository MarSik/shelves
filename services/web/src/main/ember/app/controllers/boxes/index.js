import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        createBox: function () {
            var name = this.get('name');
            var newBox = this.store.createRecord('box', {
                name: name
            });
            var self = this;

            newBox.save()
                .then(function() {
                    self.growl.info("Box created");
                })
                .catch(function() {
                    self.growl.error("Box creation failed");
                });
        }
    }
});
