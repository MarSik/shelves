import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
import inject from 'webapp/injectors/repositories';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    lotancestry: inject(),

    afterModel(model) {
      var token = this.get('session.data.authenticated.access_token');
      var ancestry = this.get('lotancestry').fetch(model.get('id'), token);
      model.set('ancestry', ancestry);
    }
});
