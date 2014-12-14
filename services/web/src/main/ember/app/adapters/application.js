import DS from 'ember-data';
import ENV from '../config/environment';

export default DS.RESTAdapter.extend({
  namespace: 'api/1',
  host: ENV.APP.API_SERVER,
});

