import Ember from 'ember';

export default Ember.View.extend({ //or Ember.Component.extend
  templateName: 'views/title-bar',
  didInsertElement: function() {
    console.log(this.$());
    console.log(this.$().foundation);
    this.$().foundation(); //or Ember.$(document).foundation();
  }
});

