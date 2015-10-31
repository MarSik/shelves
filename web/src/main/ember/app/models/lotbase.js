import DS from 'ember-data';
import IdentifiedBase from './identifiedbase';

var attr = DS.attr,
    belongsTo = DS.belongsTo;

export default IdentifiedBase.extend({
  version: attr(),
  count: attr("number"),
    created: attr("date"),

    belongsTo: belongsTo("user")
});
