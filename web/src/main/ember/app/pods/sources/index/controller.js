import Ember from 'ember';

export default Ember.Controller.extend({
    sortProperties: ['name'],
    sorted: Ember.computed.sort('model', 'sortProperties')
});
