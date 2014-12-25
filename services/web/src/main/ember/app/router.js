import Ember from 'ember';
import config from './config/environment';

var Router = Ember.Router.extend({
  location: config.locationType
});

Router.map(function() {
    this.resource('groups', function () {
        this.route('show', {path: '/:group_id'});
    });
    this.resource('boxes', function () {
        this.route('show', {path: '/:box_id'});
    });
    this.resource('footprints', function () {
        this.route('show', {path: '/:footprint_id'});
    });
    this.resource('projects', function () {
        this.route('show', {path: '/:project_id'});
    });
    this.route('account');
});

export default Router;
