import Ember from 'ember';

export default Ember.Component.extend({
  actions: {
    enablePrefix: function (prefix) {
      var unit = this.get('unit');
      var prefixes = unit.get('prefixes');
      prefixes.pushObject(prefix);
      unit.save().catch(function () {
        unit.rollbackAttributes();
      });
    },
    disablePrefix: function (prefix) {
      var unit = this.get('unit');
      var prefixes = unit.get('prefixes');
      prefixes.removeObject(prefix);
      unit.save().catch(function () {
        unit.rollbackAttributes();
      });
    }
  },

  tagName: "li",
  isEnabled: Ember.computed('model', 'unit.prefixes', function() {
    var entity = this.get('prefix');
    var enabled = this.get('unit.prefixes');
    return enabled.contains(entity);
  })

  // prefix
  // unit
});
