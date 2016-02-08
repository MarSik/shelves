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
    var ancestry = [];
    var sum = 0;

    if (this.get('purchase.transaction')) {
      ancestry.pushObject(Ember.Object.create({
        purchase: this.get('purchase'),
        history: [ this.get('history') ],
        count: this.get('count'),
        probability: 100.0
      }));

      sum += this.get('count');
    }

    var self = this;

    this.get('parents').forEach(function (p) {
      console.log("ANCESTOR ", p.get("id"), " OF ", self.get("id"));
      sum += p.get('count');
    });

    this.get('parents').forEach(function (p) {
      p.get('ancestry').forEach(function (a) {
        var hist = a.get("history")
        hist.pushObject(self.get('history'));
        ancestry.pushObject(Ember.Object.create({
            purchase: a.get('purchase'),
            history: a.get('history'),
            count: a.get('count'),
            probability: a.get('probability') * p.get('count') / sum
        }));
      });
    });

    return ancestry;
  })
});
