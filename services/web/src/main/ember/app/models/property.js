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
        var self = this;
        return DS.PromiseObject.create({
            promise: new Promise(function (resolve) {
                self.get('unit').then(function (unit) {
                    resolve(self.get('name') + "; " + (Ember.isEmpty(symbol)? '': symbol) + " [" + unit.get('symbol') + ']');
                });
            })
        });
    }.property('name', 'symbol', 'unit', 'unit.symbol'),

    link: function() {
        return "properties.show";
    }.property(),

    icon: function () {
        return "bar-chart";
    }.property()
});
