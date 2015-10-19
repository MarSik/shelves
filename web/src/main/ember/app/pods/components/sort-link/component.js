import Ember from 'ember';

export default Ember.Component.extend({
    tagName: 'span',
    actions: {
        sortUp: function () {
            this.sendAction('action', this.get('field'));
        },
        sortDown: function () {
            this.sendAction('action', this.get('field'), 'desc');
        }
    }
    // field
});
