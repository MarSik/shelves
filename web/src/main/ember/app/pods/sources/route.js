import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
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
    },
    activate: function() {
        $(document).attr('title', 'shelves - Sources');
    },
    model: function () {
        return Ember.A();
    }
});
