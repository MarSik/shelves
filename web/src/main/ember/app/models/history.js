import DS from 'ember-data';

var attr = DS.attr,
  belongsTo = DS.belongsTo;

export default DS.Model.extend({
  created: attr("date"),
  location: belongsTo("box", {async: true}),
  previous: belongsTo("history", {async: true}),
  action: attr(),
  performedBy: belongsTo('user', {async: true}),
  usedBy: belongsTo('requirement', {async: true})
});
