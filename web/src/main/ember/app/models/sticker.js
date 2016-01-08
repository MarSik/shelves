import DS from 'ember-data';

export default DS.Model.extend({
  object: DS.belongsTo('identifiedbase', {polymorphic: true})
});
