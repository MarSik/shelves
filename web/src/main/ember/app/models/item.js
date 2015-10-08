import DS from 'ember-data';
import Lot from './lot';

var hasMany = DS.hasMany;
var attr = DS.attr;
var belongsTo = DS.belongsTo;

export default Lot.extend({
    requirements: hasMany('requirement', {async: true}),
    finished: attr('boolean'),

    // only used to start projects, write only
    type: belongsTo('type', {async: true, inverse: false}),

    link: function() {
        return "items.show";
    }.property(),

    icon: function () {
        return "flask";
    }.property()
});
