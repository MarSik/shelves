import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        save: function (entity) {
            var self = this;
            entity.save().then(function (fp) {
                self.growl.info('Saved as: '+fp.get('id'));
                self.transitionTo('types.show', fp);
            }).catch(function (reason) {
                self.growl.error('Failed to save: '+reason, {clickToDismiss: true});
                entity.rollback();
            });
        }
    },

    model: function () {
        var fp = this.store.createRecord('type', {
        });
        return fp;
    },

    afterModel() {
      this.store.findAll('footprint');
      this.store.findAll('group');
    }
});
