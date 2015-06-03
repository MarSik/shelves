import Ember from 'ember';

export default Ember.Controller.extend({
    possibleTypes: Ember.computed.map('model.type', m => m),
    assignedLots: Ember.computed.map('model.lots', m => m),

    candidateLots: Ember.computed('possibleTypes.@each.lots', {
        set(key, value) {
          return value;
        },
        get() {
          var self = this;
          var req = this.get('model');

          console.log('Recomputing available parts');

          if (Ember.isEmpty(req)) {
            return [];
          }

          Ember.Promise.all(this.get('possibleTypes')).then(function (types) {
            var lots = [];
            types.forEach(function (type) {
              lots.pushObject(type.get('lots'));
            });

            Ember.Promise.all(lots).then(function (lots) {
              var usable = [];
              lots.forEach(function (lotArray) {
                lotArray.forEach(function (lot) {
                  usable.pushObject(lot);
                });
              });
              self.set('candidateLots', usable);
            });
          });

          return [];
        }
    }),

    assignableLots: Ember.computed.map('resolvedLots', m => m),
    resolvedLots: Ember.computed.filterBy('candidateLots', 'canBeAssigned', true)
});
