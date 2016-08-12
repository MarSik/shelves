import Ember from 'ember';

export default Ember.Component.extend({
  actions: {
    search(query) {
      this.get('searchAction')(query);
    }
  },
  didInsertElement: function() {
    this.$().foundation();
  },
  stickerCount: undefined,
  cartCount: undefined,
  searchAction: function(q) {},
  session: Ember.inject.service('session')
});

