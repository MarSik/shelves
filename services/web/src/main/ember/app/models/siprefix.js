import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    prefix: attr('string'),
    power: attr('number'),
    base: attr('number'),
    units: hasMany('unit', {async: true}),
    niceName: function () {
        return this.get('id') + " [" + this.get('prefix') + "] = " + this.get('base') + "^" + this.get('power');
    }.property('id', 'prefix', 'power', 'base')
});
