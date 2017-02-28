import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
import inject from 'webapp/injectors/repositories';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    userinfo: inject(),
    session: Ember.inject.service(),

    model() {
      var token = this.get('session.data.authenticated.access_token');
      return this.get('userinfo').fetch(token);
    }
});
