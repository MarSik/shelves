import DS from 'ember-data';
import LotBase from './lotbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default LotBase.extend({
  created: attr('date'),
  location: belongsTo("box", {async: true}),
  previous: belongsTo("lot", {async: true}),
  status: attr(),
  purchase: belongsTo('purchase', {async: true}),
  expiration: attr('date'),
  serials: attr(),
  history: belongsTo("history", {async: true}),

  usedBy: belongsTo('requirement', {async: true}),

  type: function () {
      return this.get('purchase.type');
  }.property('purchase.type'),

  canBeAssigned: attr(),
  canBeSoldered: attr(),
  canBeUnsoldered: attr(),
  canBeUnassigned: attr(),
  canBeSplit: attr(),
  canBeMoved: attr(),
  valid: attr(),

  icon: function () {
    if (this.get("canBeUnsoldered")) {
      return "check-square-o";
    } else {
      return "square-o";
    }
  }.property("canBeUnsoldered"),

  endpoint: function () {
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
    return this.get('purchase.type.name') + " " + this.get('serial');
  }.property('purchase.type.name', 'serial')
});
