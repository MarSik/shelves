import Ember from 'ember';

export default Ember.TextField.extend({
    type: 'file',
    attributeBindings: ['multiple', 'placeholder'],
    multiple: false,
    placeholder: 'Please select files to upload',


    observeChange: function() {
        var input = this.get('value');
        if (!Ember.isEmpty(input)) {
            this.sendAction('action', this.$()[0].files);
        }
    }.observes('value')
});
