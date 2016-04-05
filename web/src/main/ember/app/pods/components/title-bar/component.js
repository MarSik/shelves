import Ember from 'ember';

export default Ember.Component.extend({
  actions: {
    search(query) {
      this.sendAction('searchAction', query);
    }
  },
  didInsertElement: function() {
    this.$().foundation();
  },
  stickerCount: undefined,
  session: Ember.inject.service('session')
});

