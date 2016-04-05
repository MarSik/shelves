import DS from 'ember-data';
import ENV from '../config/environment';
import Ember from 'ember';
import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';

var AppAdapter =  DS.RESTAdapter.extend(DataAdapterMixin, {
  authorizer: 'authorizer:application',
  namespace: ENV.APP.API_NAMESPACE,
  host: ENV.APP.API_SERVER,
  coalesceFindRequests: true
});

var inflector = Ember.Inflector.inflector;
inflector.irregular("history", "history");

export default AppAdapter;
