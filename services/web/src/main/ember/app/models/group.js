import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
    types: hasMany("type", {async: true}),
    directCount: attr(),
    nestedCount: attr(),

    parent: belongsTo("group", {inverse: "groups", async: true}),
    groups: hasMany("group", {inverse: "parent", async: true}),
    showProperties: hasMany('property', {async: true}),

    children: function() {
        return this.get('groups');
    }.property('groups'),
    hasChildren: function() {
        return this.get('groups.length') != 0;
    }.property('groups'),
    hasParent: function() {
        // XXX this is a hack, belongsTo returns a promise..
        return !Ember.isNone(this.get('parent.content'));
    }.property('parent.content'),
    fullName: function() {
        if (Ember.isNone(this.get('parent.content'))) {
            return this.get('name');
        } else {
            return this.get('parent.content.fullName') + ' | ' + this.get('name');
        }
    }.property('parent.content', 'parent.content.fullName', 'name'),

    count: function () {
        var c = this.get('nestedCount');
        if (this.get('hasChildren')) {
            c = this.get('directCount') + ' / ' + c;
        }
        return c;
    }.property('nestedCount', 'hasChildren', 'directCount')
});
