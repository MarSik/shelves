import Ember from 'ember';

export default Ember.Component.extend({
    tagName: 'span',
    actions: {
        startEdit: function () {
            this.set('value', this.get('show'));
            this.set('editing', true);
        },
        saveEdit: function () {
            this.set('show', this.get('value'));
            this.get('content').save();
            this.set('editing', false);
        },
        cancelEdit: function () {
            this.set('editing', false);
        }
    },
    // content
    // field
    editing: false,
    value: null,
    show: Ember.computed.indirect('field')
});
