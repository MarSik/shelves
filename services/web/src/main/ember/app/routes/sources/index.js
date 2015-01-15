import Ember from 'ember';

export default Ember.Route.extend({
    model: function() {
        return this.store.filter('source', {}, function (s) {
            return !s.get('isNew');
        });
    }
});
