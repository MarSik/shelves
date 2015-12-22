import Ember from 'ember';
import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
    symbol: attr('string'),
    base: belongsTo('siprefix', {async: true, inverse: null}),
    unit: belongsTo("unit", {async: true, inverse: null}),

    niceName: Ember.computed('name', 'symbol', 'unit', 'unit.symbol', {
        set(key, value) {
            return value;
        },
        get() {
          var symbol = this.get('symbol');
          var self = this;

          this.get('unit').then(function (unit) {
            var nice = self.get('name') + "; " + (Ember.isEmpty(symbol)? '': symbol);
            if (unit) {
              nice += " [" + unit.get('symbol') + ']';
            }
            self.set('niceName', nice);
          });

          return this.get('name');
        }
    }),

    link: function() {
        return "properties.show";
    }.property(),

    icon: function () {
        return "bar-chart";
    }.property()
});
