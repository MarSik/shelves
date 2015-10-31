import DS from 'ember-data';
import IdentifiedBase from './identifiedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default IdentifiedBase.extend({
  version: attr(),
  name: attr('string'),
    description: attr('string'),
    summary: attr(),
    flagged: attr('boolean'),
    canBeDeleted: attr('boolean'),

    codes: hasMany('code', {async: true}),

    belongsTo: belongsTo('user', {async: true}),
    describedBy: hasMany("document", {async: true}),

    values: attr('object'),
    properties: hasMany('property', {async: true}),

    value: function(property) {
        var id = property.get('id');
        return this.get('values').get(id);
    },

    icon: function () {
        return "circle-thin";
    }.property()
});
