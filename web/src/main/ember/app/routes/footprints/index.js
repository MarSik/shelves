import Ember from 'ember';

export default Ember.Route.extend({
    model: function () {
        return this.store.filter('footprint', function (fp) {
            return !fp.get('isNew');
        });
    }
});
