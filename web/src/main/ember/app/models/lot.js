import DS from 'ember-data';
import LotBase from './lotbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default LotBase.extend({
  created: Ember.computed('history', function () {
    var h = this.get('history');
    var t = null;
    while (!Ember.isEmpty(h)) {
      t = h.get('validsince');
      h = h.get('previous');
    }

    return t;
  }),
  location: belongsTo("box", {async: true, inverse: "lots"}),
  previous: belongsTo("lot", {async: true, inverse: null}),
  expiration: attr('date'),
  serials: attr(),
  history: belongsTo("history", {async: true}),

  usedBy: belongsTo('requirement', {async: true}),

  type: belongsTo('type', {async: true, inverse: "lots"}),

  purchase: belongsTo('purchase', {async: true}),
  parents: hasMany("lot", {async: true, inverse: null}),

  canBeAssigned: attr(),
  canBeSoldered: attr(),
  canBeUnsoldered: attr(),
  canBeUnassigned: attr(),
  canBeSplit: attr(),
  canBeMoved: attr(),

  valid: attr(),
  used: attr(),
  usedInPast: attr(),

  icon: function () {
    if (this.get("canBeUnsoldered")) {
      return "file";
    } else if (!Ember.isEmpty(this.get("parents"))) {
      return "files-o";
    } else {
      return "file-o";
    }
  }.property("canBeUnsoldered"),

  link: function () {
    return "lots.show";
  }.property(),

  progress: function () {
    var soldered = this.get('canBeSoldered') ? 0 : this.get('count');
    return [soldered, this.get('count')];
  }.property('count', 'canBeSoldered'),

  serial: Ember.computed("serials", {
    get() {
      var serials = this.get("serials");
      return !Ember.isEmpty(serials) ? serials[0] : "";
    },
    set(k, v) {
      console.log("Set serial "+v);
      this.set("serials", [v]);
    }
  }),

  fullName: function () {
    return this.get('type.name') + " " + this.get('serial');
  }.property('type.name', 'serial'),

  ancestry: Ember.computed('purchase.transaction', 'parents.@each.ancestry', function () {
    var ancestry = {};
    var sum = 0;
    var prependHistory = [this.get('history')];

    if (this.get('purchase.transaction')) {
      // This is a primitive Lot acting as its own ancestry
      var aLotHistoryId = this.get('history.id');
      ancestry[aLotHistoryId] = Ember.Object.create({
        lot: this,
        purchase: this.get('purchase'),
        history: [ this.get('history') ],
        count: this.get('count'),
        probability: 100.0
      });

      sum += this.get('count');
    }

    var self = this;

    // Get the total count of ancestor lots
    // this is needed to compute the probability
    this.get('parents').forEach(function (p) {
      console.log("ANCESTOR ", p.get("id"), " OF ", self.get("id"), " count ", p.get("count"));
      sum += p.get('count');
    });

    // Add all ancestors to probability list
    /*
      TODO temporarily disabled pending algorithm update
    this.get('parents').forEach(function (p) {
      p.get('ancestry').forEach(function (a) {
        var newHistory = prependHistory.concat(a.get('history'));
        var aLotHistoryId = a.get('history').join();
        var probability = a.get('probability') * p.get('count') / sum;

        if (aLotHistoryId in ancestry) {
          // We already have this lot in ancestor list, just increase the probability
          var newProb = ancestry[aLotHistoryId].get('probability') + probability;
          ancestry[aLotHistoryId].set('probability', newProb);
        } else {
          // New ancestor, create new ancestor record
          ancestry[aLotHistoryId] = Ember.Object.create({
            purchase: a.get('purchase'),
            history: newHistory,
            count: a.get('count'),
            probability: probability
          });
        }
      });
    });
    */

    // Return all ancestors as list
    return Object.keys(ancestry).map(function(key){
      return ancestry[key];
    });
  })
});
