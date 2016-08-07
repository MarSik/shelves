import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
  actions: {
    addDescribes(document, obj) {
      document.get('describes').pushObject(obj);
      var self = this;
      document.save().catch(function (e) {
        self.growl.error(e);
      });
    },
    removeDescribes(document, obj) {
      document.get('describes').removeObject(obj);
      var self = this;
      document.save().catch(function (e) {
        self.growl.error(e);
      });
    }
  },
  activate: function() {
    $(document).attr('title', 'shelves - Documents');
  },
  model: function () {
    return Ember.A();
  }
});
