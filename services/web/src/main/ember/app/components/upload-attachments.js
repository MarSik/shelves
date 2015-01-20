import Ember from 'ember';
import EmberUploader from 'ember-uploader';
import ENV from '../config/environment';

export default Ember.Component.extend({
    actions: {
        selectFiles: function (files) {
            this.set('files', files);
        },
        upload: function () {
            var uploadUrl = this.get('url');
            var files = this.get('files');
            var token = this.get('controller.session.content.access_token');
            var entity = this.get('entity');

            var uploader = EmberUploader.Uploader.create({
                url: uploadUrl,
                paramName: 'files[]'
            });

            var self = this;

            uploader.on('progress', function (e) {
                self.sendAction('uploadProgress', e.percent);
            });

            uploader.on('didUpload', function (e) {
                self.sendAction('uploadFinished', e);
            });

            if (!Ember.isEmpty(files)) {
                var params = {
                    access_token: token
                };

                if (!Ember.isEmpty(entity.get('id'))) {
                    params["entity"] = entity.get('id');
                }

                uploader.upload(files, params);
            }
        }
    },
    url: function () {
        return ENV.APP.API_ENDPOINT + '/upload';
    }.property(),
    disabled: function () {
        return Ember.isEmpty(this.get('files'));
    }.property('files'),
    multiple: true
    // entity - entity to associate with the upload
});
