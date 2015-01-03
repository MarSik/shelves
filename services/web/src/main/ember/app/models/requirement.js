import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    count: attr('number'),
    type: hasMany('type', {inverse: null}),
    project: belongsTo('project'),

    typeCanBeRemoved: function () {
        return this.get('type.length') > 1
    }.property('type')
});