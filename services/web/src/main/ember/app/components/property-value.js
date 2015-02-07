import Ember from 'ember';

export default Ember.Component.extend({
    // property
    // entity
    value: function() {
        var property = this.get('property');
        var entity = this.get('entity');

        // Base SI prefix exponent and value
        var base = Math.pow(property.get('base.base'), property.get('base.power'));
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
            var unit = property.get('unit');
            var prefixes = unit.get('prefixes');

            prefixes.forEach(function (p) {
                var pPower = p.get('power');
                var pBase = p.get('base');

                var divider = Math.pow(pBase, pPower) / base;
                var value = baseValue / divider;

                if (Math.round(value) > 0 && Math.round(value).toString().length < Math.round(niceValue).toString().length) {
                    niceValue = Math.round(100 * value) / 100.0;
                    nicePrefix = p;
                }
            });

            res += nicePrefix.get('prefix');
        }

        res = niceValue + res;

        if (!Ember.isEmpty(property.get('unit.symbol'))) {
            res += property.get('unit.symbol');
        }

        return res;
    }.property('property.base.content.prefix', 'property.base.content.power', 'property.base.content.base', 'property.unit.content.symbol', 'entity', 'property.unit.content.prefixes', 'property.unit.content.prefixes.@each.power', 'property.unit.content.prefixes.@each.base', 'property.unit.content.prefixes.@each.prefix')
});
