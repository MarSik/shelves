import Ember from 'ember';

export default Ember.Controller.extend({
    _history: null,
    history: function () {
        if (this.get('_history') == null ||
                this.get('_history')[0].get('id') !== this.get('model.history.id')) {
            this.set('_history', []);
            var self = this;
            this.get('model.history').then(function (h) {
                self.set('_history', [h]);
                Ember.run.next(self, 'loadAdditionalHistory');
            });
        }

        return this.get('_history');
    }.property('_history', 'model.history.id'),

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
        }).catch(function (err) {
          console.log("Err "+err);
        });
    }
});
