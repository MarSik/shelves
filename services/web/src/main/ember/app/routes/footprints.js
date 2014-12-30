import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        createFootprint: function () {
            this.transitionTo('footprints.new');
        }
    }
});
