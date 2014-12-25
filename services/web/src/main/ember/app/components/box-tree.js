import Ember from 'ember';

export default Ember.Component.extend({
    tagName: "li",
    classNames: ["box-entity"],
    actions: {
        createBox: function() {
            this.sendAction('createBoxAction', this.get('box'));
        },
        confirmDeleteBox: function() {
            this.toggleProperty('isDeleting');
            this.set('isEditing', false);
        },
        deleteBox: function() {
            this.toggleProperty('isDeleting');
            this.set('isEditing', false);
            this.sendAction('deleteBoxAction', this.get('box'));
        },
        editBox: function() {
            this.set('isEditing', true);
        },
        editDone: function() {
            this.set('isEditing', false);
            this.sendAction('editBoxAction', this.get('box'));
        },
        selectBox: function() {
            this.sendAction('action', this.get('box'));
        },
        toggleExpand: function() {
            this.toggleProperty('isExpanded');
        }
    },
    isEditing: false,
    isDeleting: false,
    isExpanded: false,
    hasBoxes: function() {
        return this.get('box').boxes !== undefined && this.get('box').boxes != null;
    }.property('box.boxes'),
    showBoxes: function() {
        return this.get('isExpanded') && this.get('hasBoxes');
    }.property('isExpanded', 'hasBoxes')
});
