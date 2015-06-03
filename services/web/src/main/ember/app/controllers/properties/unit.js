import Ember from 'ember';

export default Ember.Controller.extend({
    needs: ['units/show'],
    enabledList: Ember.computed.alias('controllers.units/show.model.prefixes'),
    isEnabled: function() {
        var entity = this.get('model');
        var enabled = this.get('enabledList');
        return enabled.contains(entity);
    }.property('model', 'enabledList')
});
