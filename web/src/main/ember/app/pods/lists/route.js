import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        createList: function (name) {
            var lst = this.store.createRecord('list', {
                name: name
            });

            console.log("Creating list "+name);

            var self = this;

            lst.save().then(function (t) {

            }).catch(function () {
                lst.rollbackAttributes();
            });
        }
    },
    activate: function() {
        $(document).attr('title', 'shelves - Lists');
    }
});
