import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
      showCreateProject() {
        this.set('showCreateDialog', true);
      },
      startProject() {
        this.set('showCreateDialog', false);
        return true;
      }
    },

    inProgress: Ember.computed('model.@each.finished', 'model.@each.progressPct', function () {
        var model = this.get('model');
        return model.filterBy('finished', false).sortBy('progressPct', 'type.name', 'serial');
    }),
    closed: Ember.computed('model.@each.finished', function () {
        var model = this.get('model');
        return model.filterBy('finished', true).sortBy('type.name', 'serial');
    })
});
