import Ember from 'ember';

export default Ember.Route.extend({
    model: function (params) {
        return {id: params.group_id};
    }
});
