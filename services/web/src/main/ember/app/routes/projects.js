import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
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
                type: type,
                count: count
            });

            requirement.save()
        }
    },
    model: function () {
        return this.store.find('project');
    }
});
