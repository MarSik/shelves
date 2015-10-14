import Ember from 'ember';

export default Ember.Component.extend({
    tagName: 'button',
    classNameBindings: ["class"],
    attributeBindings: ["disabled"],
    confirming: false,
    done: false,
    mouseLeave: function () {
        this.set('confirming', false);
    },
    click: function () {
        if (this.get('done')) {
            return;
        }

        var c = this.get('confirming');
        if (c) {
            this.sendAction('action', this.get('param'), this.get('param2'));
            this.set('done', true);
            this.set('confirming', false);
        } else {
            this.set('confirming', true);
        }
    },
    disabled: function () {
        return this.get('done');
    }.property('confirming', 'done')
});
