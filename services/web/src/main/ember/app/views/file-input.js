import Ember from 'ember';

export default Ember.TextField.extend({
    type: 'file',
    attributeBindings: ['multiple', 'placeholder'],
    multiple: false,
    placeholder: 'Please select files to upload',
    change: function(e) {
        var input = e.target;
        if (!Ember.isEmpty(input.files)) {
            this.sendAction('action', input.files);
        }
    }
});