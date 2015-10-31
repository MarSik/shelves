import DS from 'ember-data';
import IdentifiedBase from './identifiedbase';

var attr = DS.attr,
    belongsTo = DS.belongsTo;

export default IdentifiedBase.extend({
  version: attr(),
  code: attr('string'),
    type: attr('string'),
    reference: belongsTo('namedbase', {async: true, polymorphic: true})
});
