import Ember from 'ember';

export default Ember.Controller.extend({
    application: Ember.inject.controller("application"),
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
        },
        randomCode() {
            var randomCode = this.store.createRecord('sticker', {
              object: null
            });
            randomCode.save();
        },
        fillWithRandomCodes() {
            var rem = this.get('remainingAtLast');
            for (var i = 0; i < rem; i++) {
                var randomCode = this.store.createRecord('sticker', {
                    object: null
                });
                randomCode.save();
            }
        }
    },

    papers: Ember.computed.alias('application.availablePapers'),
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
    sortAscending: true,

    pageCount: Ember.computed('paper', 'paper.stickerHorizontalCount', 'paper.stickerVerticalCount', function () {
        var page = this.get('paper');
        if (Ember.isEmpty(page)) return null;

        var x = this.get('paper.stickerHorizontalCount');
        var y = this.get('paper.stickerVerticalCount');

        if (!Ember.isEmpty(x) && !Ember.isEmpty(y)) {
            return (x*y) > 0 ? x*y : null;
        } else {
            return null;
        }
    }),

    stickersAtLast: Ember.computed('stickersTotal', 'pageCount', function() {
        var pageCount = this.get('pageCount');
        if (Ember.isEmpty(pageCount)) return null;

        var stickers = this.get('stickersTotal');
        if (stickers % pageCount) {
            return stickers % pageCount;
        } else {
            return pageCount;
        }
    }),

    remainingAtLast: Ember.computed('stickersTotal', 'pageCount', function() {
        var pageCount = this.get('pageCount');
        if (Ember.isEmpty(pageCount)) return null;

        var stickers = this.get('stickersTotal');
        if (Ember.isEmpty(stickers)) return null;

        return pageCount - (stickers % pageCount);
    }),

    pagesTotal: Ember.computed('stickersTotal', 'pageCount', function() {
        var pageCount = this.get('pageCount');
        if (Ember.isEmpty(pageCount)) return null;

        var stickers = this.get('stickersTotal');
        return Math.ceil(stickers / pageCount);
    }),

    stickersTotal: Ember.computed('model.length', 'paperSlotSkip', function() {
        return this.get('model.length') + this.get('paperSlotSkip');
    })
});
