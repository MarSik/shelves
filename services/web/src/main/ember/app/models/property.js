import Ember from 'ember';
import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
    symbol: attr('string'),
    base: belongsTo('siprefix', {async: true}),
    unit: belongsTo("unit", {async: true}),

    niceName: function () {
        var symbol = this.get('symbol');
        return this.get('name') + "; " + (Ember.isEmpty(symbol)? '': symbol) + " [" + this.get('unit.symbol') + ']';
    }.property('name', 'symbol', 'unit.symbol')
});
