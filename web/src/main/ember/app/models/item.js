import DS from 'ember-data';
import Lot from './lot';

var hasMany = DS.hasMany;
var attr = DS.attr;
var belongsTo = DS.belongsTo;

export default Lot.extend({
    requirements: hasMany('requirement', {async: true}),
    finished: attr('boolean'),

    // only used to start projects, write only
    type: belongsTo('type', {async: true, inverse: null}),

    link: function() {
        return "items.show";
    }.property(),

  icon: function () {
    if (this.get("canBeUnsoldered")) {
      return "check-square-o";
    } else if (this.get("finished")) {
      return "square-o";
    } else {
      return "flask";
    }
  }.property("canBeUnsoldered", "finished"),

  endpoint: function () {
    return "items.show";
  }.property(),

  editable: Ember.computed("finished", function () {
    return !this.get("finished");
  })
});
