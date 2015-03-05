import Ember from 'ember';

export default Ember.Component.extend({
    tagName: "li",
    classNameBindings: ["class"],
    actions: {
        nestedCreateItem: function(item) {
            this.sendAction('createAction', item);
        },
        nestedDeleteItem: function (item) {
            this.sendAction('deleteAction', item);
        },
        nestedEditItem: function(item) {
            this.sendAction('editAction', item);
        },
        nestedSelectItem: function(item) {
            this.sendAction('action', item);
        },
        nestedHoverItem: function(item) {
            this.sendAction('hoverAction', item);
        },

        createItem: function() {
            this.sendAction('createAction', this.get('item'));
        },
        confirmDeleteItem: function() {
            this.toggleProperty('isDeleting');
            this.set('isEditing', false);
            this.sendAction('deleteAction', this.get('item'));
        },
        cancelDeleteItem: function() {
            this.toggleProperty('isDeleting');
        },
        deleteItem: function() {
            this.toggleProperty('isDeleting');
            this.set('isEditing', false);
        },
        editItem: function() {
            this.set('isEditing', true);
        },
        editDone: function() {
            this.set('isEditing', false);
            this.sendAction('editAction', this.get('item'));
        },
        selectItem: function() {
            this.sendAction('action', this.get('item'));
        },
        toggleExpand: function() {
            this.toggleProperty('isExpanded');
        },
        dropped: function (zoneId, itemId) {
            console.log('drop noticed');
            this.sendAction('moveItem', itemId, zoneId);
        },
        nestedMove: function (itemId, zoneId) {
            this.sendAction('moveItem', itemId, zoneId);
        }
    },
    mouseEnter: function() {
        this.sendAction('hoverAction', this.get('item'));
    },
    isEditing: false,
    isDeleting: false,
    isExpanded: false,
    hasChildren: Ember.computed.alias('item.hasChildren'),
    showChildren: function() {
        return this.get('isExpanded') && this.get('hasChildren');
    }.property('isExpanded', 'hasChildren'),
    sorting: ['fullName'],
    sorted: Ember.computed.sort('item.children', 'sorting'),
    auxInfo: Ember.computed.indirect('count')
});
