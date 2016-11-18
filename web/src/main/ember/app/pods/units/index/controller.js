import Ember from 'ember';

export default Ember.Controller.extend({
  actions: {
    showCreateUnit: function () {
      this.set('showCreateDialog', true);
    },
    createUnit: function(name, symbol) {
      this.set('showCreateDialog', false);
      return true;
    }
  },
    sortProperties: ['name'],
    sorted: Ember.computed.sort('model', 'sortProperties'),
    createDisabled: function () {
        return Ember.isEmpty(this.get('unitName'));
    }.property('unitName')
});
