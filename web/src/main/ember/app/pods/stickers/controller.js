import Ember from 'ember';

export default Ember.ArrayController.extend({
    needs: ['application'],
    actions: {
        clearStickers: function () {
            console.log('Clearing sticker...');
            this.clear();
        },
        removeSticker: function (sticker) {
            this.removeObject(sticker);
        }
    },

    papers: Ember.computed.alias('controllers.application.availablePapers'),
    paper: null,

    knownPaper: function() {
        return Ember.isEmpty(this.get('paper')) || !this.get('paper.custom');
    }.property('paper', 'paper.custom'),

    fieldEditable: function () {
        if (this.get('knownPaper')) {
            return "readonly";
        } else {
            return undefined;
        }
    }.property('knownPaper'),

    paperNotSelected: function() {
        return Ember.isEmpty(this.get('paper'));
    }.property('paper'),

    paperDetailStyle: function () {
        if (this.get('knownPaper')) {
            return "display: none;";
        } else {
            return "";
        }
    }.property('knownPaper'),

    cannotPrint: function () {
        return this.get('paperNotSelected') || Ember.isEmpty(this.get('model'));
    }.property('paperNotSelected', 'model.@each')
});
