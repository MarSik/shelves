import DS from 'ember-data';

var attr = DS.attr;

export default DS.Model.extend({
  version: attr(),
    name: attr('string')
});
