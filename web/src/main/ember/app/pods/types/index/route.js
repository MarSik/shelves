import Ember from 'ember';

export default Ember.Route.extend({
    model: function () {
        return this.store.filter('type', function (type) {
            return !type.get('isNew');
        });
    }
});
