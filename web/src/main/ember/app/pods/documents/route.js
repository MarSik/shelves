import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
  activate: function() {
    $(document).attr('title', 'shelves - Documents');
  },
  model: function () {
    return Ember.A();
  }
});
