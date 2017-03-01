import Ember from 'ember';

export default Ember.Component.extend({
  didInsertElement: function() {
    this.$().foundation();
  },
  stickerCount: undefined,
  cartCount: undefined,
  searchAction: function(q) {},
  session: Ember.inject.service('session')
});

