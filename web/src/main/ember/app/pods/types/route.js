import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        createType: function () {
            this.transitionTo('types.new');
        },
        deleteType: function (type) {
            type.destroyRecord();
        },
        startProject: function (type, name) {
            var project = this.store.createRecord('item', {
                serial: name,
                type: type,
                count: 1
            });

            var self = this;

            project.save().then(function (p) {
                self.transitionTo('items.show', p);
            }).catch(function () {
                project.rollback();
            });
        }
    },
    activate: function() {
        $(document).attr('title', 'shelves - Part types');
    },
    model: function () {
        return Ember.A();
    }
});
