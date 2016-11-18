import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        save: function (entity) {
            var self = this;
            entity.save().then(function (fp) {
                self.growl.info('Saved as: '+fp.get('id'));
                self.transitionTo('footprints.show', fp);
            }).catch(function (reason) {
                self.growl.error('Failed to save: '+reason);
                entity.rollbackAttributes();
            });
        }
    },

    model: function () {
        var fp = this.store.createRecord('footprint', {
            name: "New footprint"
        });
        return fp;
    }
});
