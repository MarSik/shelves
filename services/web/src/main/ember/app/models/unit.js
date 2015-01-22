import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    symbol: attr("string"),
    prefixes: hasMany('isoprefix', {async: true})
});