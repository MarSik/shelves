import Ember from 'ember';

export default Ember.ArrayController.extend({
    actions: {
        showCreateBox: function (box) {
            this.set('selectedBox', box);
            $("#createBox").foundation("reveal", "open");
        },
        showCreateTopLevelBox: function () {
            this.set('selectedBox', null);
            $("#createBox").foundation("reveal", "open");
        },
        createBox: function () {
            var name = this.get('name');
            var newBox = this.store.createRecord('box', {
                name: name,
                parent: this.get('selectedBox')
            });
            var self = this;
            $("#createBox").foundation("reveal", "close");

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
        },
        deleteBox: function(box) {
            box.destroyRecord();
        },
        selectBox: function(box) {
            this.set('selectedBox', box);
        },
        hoverBox: function(box) {
            this.set('selectedBox', box);
        }
    },
    sortProperties: ['name'],
    sortAscending: true
});
