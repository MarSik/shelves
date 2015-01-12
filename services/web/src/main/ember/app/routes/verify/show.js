import Ember from 'ember';
import ENV from '../../config/environment';

export default Ember.Route.extend({
    model: function(params) {
        var url = ENV.APP.API_ENDPOINT + '/users/verify/' + params.token;
        return $.ajax({
            type: "POST",
            url: url
        });
    }
});