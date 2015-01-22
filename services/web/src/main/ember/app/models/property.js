import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
    base: belongsTo('isoprefix', {async: true}),
    belongsTo: belongsTo("unit", {async: true}),
    groups: hasMany('group', {async: true})
});
