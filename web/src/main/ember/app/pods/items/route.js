import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        addAlternativePart: function (req, type) {
            console.log("Adding alternative");
            req.get('type').pushObject(type);
            req.save();
        },
        removeAlternativePart: function (req, type) {
            console.log("Removing alternative");
            req.get('type').removeObject(type);
            req.save();
        },
        removeRequirement: function (req) {
            req.destroyRecord();
        },
        projectSelected: function (project) {
            this.transitionTo('items.show', project);
        },
        startProject: function (name) {
            var type = this.store.createRecord('type', {
                name: name,
                manufacturable: true,
                serials: true
            });

            console.log("Creating project "+name);

            var self = this;

            type.save().then(function (t) {
                var project = self.store.createRecord('item', {
                    serial: name,
                    type: t
                });

                project.save().then(function (p) {
                    self.transitionTo('items.show', p);
                }).catch(function () {
                    project.rollbackAttributes();
                    self.transitionTo('types.show', t);
                });
            }).catch(function () {
                type.rollbackAttributes();
            });
        },
        addRequirement: function (project, type, count) {
            var requirement = this.store.createRecord('requirement', {
                item: project,
                count: count
            });

            requirement.get('type').pushObject(type);
            requirement.save();
        }
    },
    activate: function() {
        $(document).attr('title', 'shelves - Projects');
    },
    model: function () {
        return Ember.A();
    }
});
