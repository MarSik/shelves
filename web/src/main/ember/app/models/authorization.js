import DS from 'ember-data';
import IdentifiedBase from './identifiedbase';

var attr = DS.attr;

export default IdentifiedBase.extend({
  version: attr(),
    name: attr('string')
});
