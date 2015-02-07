import Ember from 'ember';

export default Ember.Controller.extend({
    _history: null,
    history: function () {
        if (this.get('_history') == null) {
            this.set('_history', [this.get('model')]);

            Ember.run.next(this, 'loadAdditionalHistory');
        }

        return this.get('_history');
    }.property('_history'),

    loadAdditionalHistory: function () {
        var history = this.get('_history');
        var self = this;

        if (Ember.isEmpty(history)) {
            return;
        }

        var latest = history[ history.length - 1 ];
        latest.get('previous').then(function (p) {
            if (Ember.isEmpty(p)) {
                return;
            }

            history.pushObject(p);
            Ember.run.next(self, 'loadAdditionalHistory');
        });
    }
});
