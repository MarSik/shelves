import DS from 'ember-data';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: attr(),

    types: hasMany("type", {async: true}),

    parent: belongsTo("group", {inverse: "groups", async: true}),
    groups: hasMany("group", {inverse: "parent", async: true}),
    belongsTo: belongsTo("user", {async: true}),

    children: function() {
        return this.get('groups');
    }.property('groups'),
    hasChildren: function() {
        return this.get('groups.length') != 0;
    }.property('groups'),
    hasParent: function() {
        // XXX this is a hack, belongsTo returns a promise..
        return !Ember.isNone(this.get('parent.content'));
    }.property('parent.content')
});
