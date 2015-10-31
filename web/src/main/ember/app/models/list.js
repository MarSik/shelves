import Ember from 'ember';
import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
  hasMany = DS.hasMany,
  belongsTo = DS.belongsTo;

export default NamedBase.extend({
  items: hasMany("identifiedbase", {async: true, inverse: null, polymorphic: true})
});
