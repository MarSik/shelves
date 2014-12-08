import Ember from 'ember';
import config from './config/environment';

var Router = Ember.Router.extend({
  location: config.locationType
});

Router.map(function() {
    this.route('login');
    this.resource('groups', function () {
        this.route('new');
        this.route('show', {path: '/:group_id'});
    });
    this.resource('lots', function () {
        this.route('show', {path: '/:lot_id'});
    });
});

export default Router;
