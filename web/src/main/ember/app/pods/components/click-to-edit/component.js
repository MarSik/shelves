import Ember from 'ember';

export default Ember.Component.extend({
    tagName: 'span',
    actions: {
        startEdit: function () {
            if (!this.get("enabled")) {
                return;
            }

            this.set('value', this.get('show'));
            this.set('editing', true);
        },
        saveEdit: function () {
            this.set('show', this.get('value'));
            this.set(this.get('field'), this.get('value')); // workaround for https://github.com/gordonkristan/ember-computed-indirect/issues/10
            console.log("Edit save: ", this.get('value'), " -> ", this.get('field'), " = ", this.get(this.get('field')), ' = ', this.get('show'));
            this.get('content').save();
            this.set('editing', false);
        },
        cancelEdit: function () {
            this.set('editing', false);
        }
    },
    content: undefined,
    field: undefined,
    editable: true,

    editing: false,
    value: null,
    show: Ember.computed.indirect('field'),
    enabled: Ember.computed("editable", function () {
      var e = this.get("editable");
      return Ember.isNone(this.get('editable')) || this.get('editable');
    })
});
