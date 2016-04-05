import Ember from 'ember';
import ENV from '../../../config/environment';

/* global Webcam */

export default Ember.Component.extend({
    actions: {
        startCamera: function () {
            this.set('enabled', true);
            Webcam.setSWFLocation("/assets/webcam.swf");
            Webcam.set({
                width: 640,
                height: 480
            });
            Webcam.attach('cameraViewport');
        },
        pauseCamera: function () {
            Webcam.freeze();
            this.set('frozen', true);
        },
        unpauseCamera: function () {
            Webcam.unfreeze();
            this.set('frozen', false);
        },
        cameraSnap: function () {
            var self = this;

            var token = this.get('session.content.secure.access_token');
            var entity = this.get('entity');
            var uploadUrl = this.get('url') + '?access_token=' + token + '&entity=' + entity.get('id');

            Webcam.snap( function(data_uri) {
                self.set('uploading', true);

                Webcam.upload( data_uri, uploadUrl, function(code, text) {
                    // Upload complete!
                    // 'code' will be the HTTP response code from the server, e.g. 200
                    // 'text' will be the raw response content
                    self.sendAction('uploadFinished', JSON.parse(text));
                    self.set('uploading', false);
                });
            });
            this.set('frozen', false);
        },
        stopCamera: function () {
            Webcam.reset();
            this.set('enabled', false);
        }
    },
    session: Ember.inject.service('session'),
    frozen: false,
    enabled: false,
    uploading: false,
    viewportClass: function () {
        if (this.get('enabled')) {
            return "enabled";
        } else {
            return "";
        }
    }.property('enabled'),
    url: function () {
        return ENV.APP.API_ENDPOINT + '/upload';
    }.property()
});
