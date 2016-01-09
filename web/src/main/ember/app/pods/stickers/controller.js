import Ember from 'ember';

export default Ember.ArrayController.extend({
    needs: ['application'],
    actions: {
        clearStickers: function () {
            console.log('Clearing sticker...');
            this.store.findAll('sticker').then(function (arr) {
              arr.forEach(function (st) {
                st.destroyRecord();
              })
            });
        },
        removeSticker: function (sticker) {
            sticker.destroyRecord();
        },
        addSticker: function (sticker) {
            var ns = this.store.createRecord('sticker', {
              object: sticker.get('object')
            });
            ns.save();
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
        if (this.get('paperNotSelected')) {
          return "unselected-paper";
        } else if (this.get('knownPaper')) {
            return "known-paper";
        } else {
            return "";
        }
    }.property('knownPaper', 'paperNotSelected'),

    cannotPrint: function () {
        return this.get('paperNotSelected') || Ember.isEmpty(this.get('model'));
    }.property('paperNotSelected', 'model.@each'),

    paperSlotSkip: 0,

    sortProperties: ['fullName'],
    sortAscending: true
});
