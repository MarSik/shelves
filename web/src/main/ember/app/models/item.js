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

  icon: function () {
    if (this.get("canBeUnsoldered")) {
      return "check-square-o";
    } else if (this.get("finished")) {
      return "square-o";
    } else {
      return "flask";
    }
  }.property("canBeUnsoldered", "finished"),

  link: function () {
    return "items.show.index";
  }.property(),

  editable: Ember.computed("finished", function () {
    return !this.get("finished");
  }),

  progress: function () {
    var sum = 0;
    var soldered = 0;

    this.get('requirements').forEach(function (req) {
      var prog = req.get('progress');
      soldered += prog[0];
      sum += prog[1];
    });

    return [soldered, sum];
  }.property('requirements.@each.progress'),

  progressPct: function() {
    var progress = this.get('progress');
    if (progress[1] == 0) return 0;

    return Math.round(1000.0 * progress[0] / progress[1]) / 10.0;
  }.property('progress')
});
