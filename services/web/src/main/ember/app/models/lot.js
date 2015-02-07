import Ember from 'ember';
import DS from 'ember-data';
import LotBase from './lotbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default LotBase.extend({
  created: attr('date'),
  location: belongsTo("box", {async: true}),
  previous: belongsTo("lot", {inverse: "next", async: true}),
  next: hasMany("lot", {inverse: "previous", async: true}),
  action: attr(),
  purchase: belongsTo('purchase', {inverse: null, async: true}),
  expiration: attr('date'),
  serial: attr('string'),

  performedBy: belongsTo('user', {inverse: null, async: true}),
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
  valid: attr()
});
