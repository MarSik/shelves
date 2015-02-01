import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr('string'),
    summary: attr('string'),
    count: attr('number'),
    type: hasMany('type', {inverse: null, async: true}),
    project: belongsTo('project', {async: true}),
    lots: hasMany('lot', {async: true}),

    typeCanBeRemoved: function () {
        return this.get('type.length') > 1;
    }.property('type'),

    missing: function () {
        var assigned = 0;
        this.get('lots').then(function (lots) {
            lots.forEach(function (lot) {
                assigned += lot.get('count');
            });
        });
        return this.get('count') - assigned;
    }.property('lots.@each.count', 'count')
});
