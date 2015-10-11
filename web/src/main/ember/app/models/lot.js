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
  serial: attr('string'),
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
    return "square-o";
  }.property(),

  endpoint: function () {
    return "lots.show";
  }.property()
});
