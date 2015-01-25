import Ember from 'ember';

export default Ember.Component.extend({
    // property
    // entity
    value: function() {
        var property = this.get('property');
        var entity = this.get('entity');

        // Base iso prefix exponent and value
        var base = property.get('base.power10');
        var baseValue = entity.get('values.'+property.get('id'));

        if (Ember.isEmpty(baseValue)) {
            return "—";
        }

        // Nice value and prefix object
        var niceValue = baseValue;
        var nicePrefix = property.get('base.content');

        var res =  " ";

        if (!Ember.isEmpty(nicePrefix)) {
            // Prefixes available for unit
            var unit = property.get('unit.content');
            var prefixes = unit.get('prefixes.content');

            if (!Ember.isEmpty(prefixes)) {
                prefixes.forEach(function (p) {
                    var prefix = p.get('power10');
                    var divider = prefix - base;
                    var value = baseValue / Math.pow(10, divider);

                    if (value > 0 && value.toString().length < niceValue.toString().length) {
                        niceValue = value;
                        nicePrefix = p;
                    }
                });
            }

            res += nicePrefix.get('prefix');
        }

        res = niceValue + res;

        if (!Ember.isEmpty(property.get('unit.symbol'))) {
            res += property.get('unit.symbol');
        }

        return res;
    }.property('property.base.content.prefix', 'property.base.content.power10', 'property.unit.content.symbol', 'entity', 'property.unit.content.prefixes', 'property.unit.content.prefixes.@each.power10', 'property.unit.content.prefixes.@each.prefix')
});
