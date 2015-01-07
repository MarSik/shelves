import Ember from 'ember';

export default Ember.Component.extend({
    classNames: ['draggable-dropzone'],
    classNameBindings: ['dragClass'],
    dragClass: 'deactivated',

    dragLeave: function(event) {
        event.preventDefault();
        this.set('dragClass', 'deactivated');
    },

    dragOver: function(event) {
        event.preventDefault();
        this.set('dragClass', 'activated');
    },

    drop: function(event) {
        event.preventDefault();
        this.set('dragClass', 'deactivated');
        var data = event.dataTransfer.getData('text/data');
        // default drop action - change with {{draggable-dropzone dropped=xyz}}
        console.log('dropped');
        this.sendAction('dropped', this.get('zone'), data);
    }
});
