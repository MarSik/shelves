import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        createSource: function() {
            this.transitionTo('sources.new');
        },
        saveSource: function(source) {
            var self = this;
            source.save().then(function () {
                self.transitionTo('sources.show', source);
            }).catch(function (reason) {
                self.growl.error(reason);
            });
        }
    }
});
