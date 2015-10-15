import DS from 'ember-data';
import ENV from '../config/environment';
import Ember from 'ember';

var AppAdapter =  DS.RESTAdapter.extend({
  namespace: ENV.APP.API_NAMESPACE,
  host: ENV.APP.API_SERVER,
  coalesceFindRequests: true
});

var inflector = Ember.Inflector.inflector;
inflector.irregular("history", "history");

export default AppAdapter;
