import DS from 'ember-data';
import Lot from './lot';

var hasMany = DS.hasMany;
var attr = DS.attr;

export default Lot.extend({
    requirements: hasMany('requirement', {async: true}),
    finished: attr('boolean'),

    link: function() {
        return "items.show";
    }.property(),

    icon: function () {
        return "flask";
    }.property()
});
