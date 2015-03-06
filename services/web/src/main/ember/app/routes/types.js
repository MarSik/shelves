import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        createType: function () {
            this.transitionTo('types.new');
        },
        deleteType: function (type) {
            type.destroyRecord();
        }
    },
    activate: function() {
        $(document).attr('title', 'shelves - Part types');
    },
    model: function () {
        return this.store.all('type');
    }
});
