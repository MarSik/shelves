import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
  type: belongsTo('footprinttype', {async: true}),
  kicad: attr(),

  pads: attr("number"),
  holes: attr("number"),
  npth: attr("number", {defaultValue: 0}),
  pitch: attr(),


  seeAlso: hasMany("footprint", {async: true, inverse: "seeAlso"}),

  link: function() {
      return "footprints.show";
  }.property(),

  icon: function () {
      return "th-large";
  }.property()
});
