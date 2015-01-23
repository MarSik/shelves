import Ember from 'ember';

export default Ember.ObjectController.extend({
    needs: ['types/show'],
    entity: Ember.computed.alias('controllers.types/show.model'),
    value: function() {
        var property = this.get('model');
        var entity = this.get('entity');
        return entity.get('values.'+property.get('id'));
    }.property('entity')
});