import Ember from 'ember';

export default Ember.Component.extend({
  tagName: '',
  value: undefined,
  cond: undefined,

  fullyLoaded: Ember.computed.alias('cond.isFulfilled')
});
