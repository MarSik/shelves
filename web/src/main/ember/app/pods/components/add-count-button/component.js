import Ember from 'ember';

export default Ember.Component.extend({
    tagName: 'span',
    actions: {
        startEdit: function () {
            this.set('value', this.get('default'));
            this.set('editing', true);
        },
        saveEdit: function () {
          console.log("Save lot count ", this.get('action'), this.get('value'));
            this.get('action')(this.get('value'));
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
