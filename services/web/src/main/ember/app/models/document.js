import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr('string'),
    summary: attr(),
    description: attr(),
    url: attr('string'),
    contentType: attr('string'),
    size: attr('number'),
    created: attr('date'),
    belongsTo: belongsTo('user', {async: true}),
    describes: hasMany('namedbase', {async: true, polymorphic: true}),

    link: function() {
        return "documents.show";
    }.property(),

    icon: function () {
        return "file-text-o";
    }.property()
});
