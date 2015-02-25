import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
    requirements: hasMany('requirement', {async: true}),

    link: function() {
        return "projects.show";
    }.property(),

    icon: function () {
        return "flask";
    }.property()
});
