import Ember from 'ember';

export default Ember.ObjectController.extend({
    icon: function () {
        var ct = this.get('model.contentType');
        if (ct == "text/plain") {
            return "file-text-o";
        } else if (ct == "application/pdf") {
            return "file-pdf-o";
        } else if (ct == "image/jpeg") {
            return "file-image-o";
        } else if (ct == "image/png") {
            return "file-image-o";
        } else if (ct == "image/gif") {
            return "file-image-o";
        } else if (ct == "application/x-kicad-schematic") {
            return "file";
        } else {
            return "file-o";
        }
    }.property('model.contentType')
});
