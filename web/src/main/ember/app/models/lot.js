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
  history: belongsTo("history", {async: true, inverse: null}),

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

  ancestry: attr(),

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

  endpoint: function () {
    return "lots.show";
  }.property()
});
