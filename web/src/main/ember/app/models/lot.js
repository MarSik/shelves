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
  location: belongsTo("box", {async: true}),
  previous: belongsTo("lot", {async: true, inverse: null}),
  status: attr(),
  expiration: attr('date'),
  serials: attr(),
  history: belongsTo("history", {async: true}),

  usedBy: belongsTo('requirement', {async: true}),

  type: belongsTo('type', {async: true, inverse: null}),

  purchase: belongsTo('purchase', {async: true}),
  parents: hasMany("lot", {async: true, inverse: null}),

  canBeAssigned: attr(),
  canBeSoldered: attr(),
  canBeUnsoldered: attr(),
  canBeUnassigned: attr(),
  canBeSplit: attr(),
  canBeMoved: attr(),
  valid: attr(),

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

    if (this.get('purchase.transaction')) {
      // This is a primitive Lot acting as its own ancestry
      ancestry[this.get('id')] = Ember.Object.create({
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
      console.log("ANCESTOR ", p.get("id"), " OF ", self.get("id"));
      sum += p.get('count');
    });

    // Add all ancestors to probability list
    this.get('parents').forEach(function (p) {
      p.get('ancestry').forEach(function (a) {
        var aLotId = a.get('lot.id');
        var probability = a.get('probability') * p.get('count') / sum;

        if (aLotId in ancestry) {
          // We already have this lot in ancestor list, just increase the probability
          var newProb = ancestry[aLotId].get('probability') + probability;
          ancestry[aLotId].set('probability', newProb);
        } else {
          // New ancestor, create new ancestor record
          ancestry[aLotId] = Ember.Object.create({
            purchase: a.get('purchase'),
            lot: a.get('lot'),
            history: [self.get('history')].concat(a.get('history')),
            count: a.get('count'),
            probability: probability
          });
        }
      });
    });

    // Return all ancestors as list
    return Object.keys(ancestry).map(function(key){
      return ancestry[key];
    });
  })
});
