import DS from 'ember-data';
import NamedBase from './namedbase';

var hasMany = DS.hasMany;

export default NamedBase.extend({
    requirements: hasMany('requirement', {async: true}),

    link: function() {
        return "projects.show";
    }.property(),

    icon: function () {
        return "flask";
    }.property()
});
