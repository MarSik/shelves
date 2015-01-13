import Ember from 'ember';

export default Ember.Component.extend({
    actions: {
        startEdit: function () {
            this.set('value', this.get(this.get('field')));
            this.set('editing', true);
        },
        saveEdit: function () {
            this.set(this.get('field'), this.get('value'));
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
    show: function() {
        return this.get(this.get('field'));
    }.property('content', 'field')
});
