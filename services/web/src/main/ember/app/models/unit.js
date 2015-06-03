import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany;

export default NamedBase.extend({
    symbol: attr("string"),
    prefixes: hasMany('siprefix', {async: true}),

    niceName: function () {
        return this.get('name') + " [" + this.get('symbol') + ']';
    }.property('name', 'symbol'),

    link: function() {
        return "units.show";
    }.property(),

    icon: function () {
        return "arrows";
    }.property()
});
