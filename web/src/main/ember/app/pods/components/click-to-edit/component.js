import Ember from 'ember';

export default Ember.Component.extend({
    tagName: 'span',
    actions: {
        startEdit: function () {
            if (!this.get("enabled")) {
                return;
            }

            this.set('value', this.get('realValue'));
            this.set('editing', true);
        },
        saveEdit: function () {
            console.log('Saving '+this.get('value'));
            this.set('realValue', this.get('value'));
            console.log('Saved '+this.get('realValue'));
            console.log('Saved '+this.get(this.get('field')));
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
    realValue: Ember.computed.indirect('field'),
    enabled: Ember.computed("editable", function () {
      var e = this.get("editable");
      return Ember.isNone(this.get('editable')) || this.get('editable');
    })
});
