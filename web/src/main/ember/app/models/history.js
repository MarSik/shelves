import DS from 'ember-data';

var attr = DS.attr,
  belongsTo = DS.belongsTo;

export default DS.Model.extend({
  validSince: attr("date"),
  location: belongsTo("box", {async: true, inverse: null}),
  previous: belongsTo("history", {async: true, inverse: null}),
  action: attr(),
  performedBy: belongsTo('user', {async: true, inverse: null}),
  usedBy: belongsTo('requirement', {async: true, inverse: null})
});
