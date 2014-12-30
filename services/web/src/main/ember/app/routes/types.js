import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        createType: function () {
            this.transitionTo('types.new');
        },
        deleteType: function (type) {
            type.destroyRecord();
        }
    }
});
