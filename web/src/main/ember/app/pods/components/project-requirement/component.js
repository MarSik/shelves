import Ember from 'ember';
// global Promise

export default Ember.Component.extend({
  tagName: "tbody",

  actions: {
    solderLot(lot, count) {
      this.sendAction("solderLot", lot, count);
    },
    unsolderLot(lot, count) {
      this.sendAction("unsolderLot", lot, count);
    },
    unassignLot(lot, count) {
      this.sendAction("unassignLot", lot, count);
    },
    assignLot() {
      this.sendAction("assignLot", this.get("r"), this.get('assignableLots'));
    },
    showAddAlternative() {
      this.sendAction("showAddAlternative", this.get("r"));
    },
    removeRequirement() {
      this.sendAction("removeRequirement", this.get("r"));
    },
    removeAlternativePart(type) {
      this.sendAction("removeAlternativePart", this.get("r"), type);
    }
  },

  possibleTypes: Ember.computed.map('r.type', m => m),
  assignedLots: Ember.computed.filterBy('r.lots', 'valid', true),

  candidateLots: Ember.computed('possibleTypes.@each.lots', {
    set(key, value) {
      return value;
    },
    get() {
      var self = this;
      var req = this.get('r');

      console.log('Recomputing available parts');

      if (Ember.isEmpty(req)) {
        return [];
      }

      Promise.all(this.get('possibleTypes')).then(function (types) {
        var lots = [];
        types.forEach(function (type) {
          lots.pushObject(type.get('lots'));
        });

        Promise.all(lots).then(function (lots) {
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

  // r
});
