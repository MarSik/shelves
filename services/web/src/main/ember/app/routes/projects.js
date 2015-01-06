import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';

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
            this.transitionTo('projects.show', project);
        },
        startProject: function (name) {
            var project = this.store.createRecord('project', {
                name: name
            });
            console.log("Creating project "+name);

            project.save().then(function (p) {
                this.transitionTo('projects.show', p);
            }).catch(function () {
                project.rollback();
            })
        },
        addRequirement: function (project, type, count) {
            var requirement = this.store.createRecord('requirement', {
                project: project,
                count: count
            });

            requirement.get('type').pushObject(type);
            requirement.save()
        }
    },
    model: function () {
        return this.store.find('project');
    },
    setupController: function (controller, model) {
        controller.set('model', model);
    }
});
