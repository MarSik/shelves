import Ember from 'ember';

export default Ember.Route.extend({
    model: function() {
        return this.store.filter('box', {}, function (box) {
            // return only top level boxes
            return !box.get('hasParent');
        });
    }
});
