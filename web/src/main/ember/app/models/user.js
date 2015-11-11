import DS from 'ember-data';

export default DS.Model.extend({
  version: DS.attr(),
  name:     DS.attr('string'),
    email:    DS.attr('string'),
    password: DS.attr('string'),
  currency: DS.attr('string'),
    authorizations: DS.hasMany('authorization', {async: true, inverse: null}),
  projectSource: DS.belongsTo('source', {async: true, inverse: null}),
  lostAndFound: DS.belongsTo('group', {async: true, inverse: null})
});
