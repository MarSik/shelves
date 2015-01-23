import Ember from 'ember';

export default Ember.ArrayController.extend({
    sortProperties: ['name'],
    sortAscending: true,
    createDisabled: function () {
        return Ember.isEmpty(this.get('unitName'));
    }.property('unitName')
});
