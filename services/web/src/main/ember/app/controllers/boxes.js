import Ember from 'ember';
/* global $ */

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
            return true;
        },
        hoverBox: function(box) {
            this.set('selectedBox', box);
        }
    },
    sortProperties: ['name'],
    sortAscending: true,
    updateQr: function() {
        if (!Ember.isEmpty(this.get('selectedBox'))) {
            this.send('showQr', 'boxes', this.get('selectedBox'));
        } else {
            this.send('hideQr');
        }
    }.observes('selectedBox')
});
