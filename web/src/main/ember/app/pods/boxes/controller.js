import Ember from 'ember';
/* global $ */

export default Ember.ArrayController.extend({
    actions: {
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
    }.observes('selectedBox'),
    topLevel: Ember.computed.filterBy('model', 'hasParent', false)
});
