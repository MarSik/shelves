import Ember from 'ember';

export default Ember.ArrayController.extend({
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
    sortAscending: true,
    createDisabled: function () {
        return Ember.isEmpty(this.get('unitName'));
    }.property('unitName')
});
