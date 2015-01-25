import Ember from 'ember';

export default Ember.ObjectController.extend({
    needs: ['types/show'],
    entity: Ember.computed.alias('controllers.types/show.model'),
    value: function() {
        var property = this.get('model');
        var entity = this.get('entity');
        var res =  entity.get('values.'+property.get('id')) + " ";

        if (!Ember.isEmpty(property.get('base.prefix'))) {
            res += property.get('base.prefix');
        }

        if (!Ember.isEmpty(property.get('unit.symbol'))) {
            res += property.get('unit.symbol');
        }

        return res;
    }.property('model.base.prefix', 'model.unit.symbol', 'entity')
});