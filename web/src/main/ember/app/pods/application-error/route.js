import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';
import inject from 'webapp/injectors/repositories';

export default Ember.Route.extend(ApplicationRouteMixin, {
    session: Ember.inject.service('session'),
    actions: {
        search: function(query) {
            var search = this.store.createRecord('search', {
                query: query
            });
            var self = this;
            search.save().then(function (result) {
                self.transitionTo('search.result', result);
            });
        },
        invalidateSession() {
            this.get('session').invalidate();
        }
    }
});
