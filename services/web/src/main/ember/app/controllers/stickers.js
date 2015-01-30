import Ember from 'ember';

export default Ember.ArrayController.extend({
    actions: {
        clearStickers: function () {
            console.log('Clearing sticker...');
            this.clear();
        },
        removeSticker: function (sticker) {
            this.removeObject(sticker);
        }
    }
});
