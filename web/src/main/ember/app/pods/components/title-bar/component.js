import Ember from 'ember';

export default Ember.Component.extend({ //or Ember.Component.extend
  didInsertElement: function() {
    this.$().foundation();
  },
  stickerCount: undefined
});

