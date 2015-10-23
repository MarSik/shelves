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

  serial: Ember.computed("serials", {
    get() {
      var serials = this.get("serials");
      return serials.length ? serials[0] : "";
    },
    set(k, v) {
      console.log("Set serial "+v);
      this.set("serials", [v]);
    }
  })
});
