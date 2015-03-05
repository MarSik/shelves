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

    missing: function (key, value) {
        if (arguments.length > 1) {
            return value;
        }

        var self = this;
        this.get('lots').then(function (lots) {
            var assigned = 0;
            lots.forEach(function (lot) {
                assigned += lot.get('count');
            });
            self.set('missing', self.get('count') - assigned);
        });
        return 0;
    }.property('lots.@each.count', 'count')
});
