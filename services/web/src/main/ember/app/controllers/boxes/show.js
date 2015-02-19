import Ember from 'ember';

export default Ember.Controller.extend({
    lots: Ember.computed.filterBy('model.lots', 'valid', true)
});
