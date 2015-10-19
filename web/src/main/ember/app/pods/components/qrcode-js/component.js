import Ember from 'ember';
// global QRCode

export default Ember.Component.extend({
    didInsertElement: function () {
        var qrcode = new QRCode(this.$().get(0), {
            text: this.get('url'),
            width: this.get('size'),
            height: this.get('size'),
            correctLevel : QRCode.CorrectLevel.H
        });
    }
});
