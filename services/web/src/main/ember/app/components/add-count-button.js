import Ember from 'ember';

export default Ember.Component.extend({
    actions: {
        startEdit: function () {
            this.set('value', this.get('default'));
            this.set('editing', true);
        },
        saveEdit: function () {
            this.sendAction('action', this.get('param'), this.get('param2'), this.get('value'));
            this.set('editing', false);
        },
        cancelEdit: function () {
            this.set('editing', false);
        }
    },
    // param
    // param2
    // default
    editing: false,
    value: 0
});
