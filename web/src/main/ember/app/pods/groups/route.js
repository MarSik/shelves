import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
// global $

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        moveItem: function (itemId, groupId) {
            var self = this;
            console.log('drop processing');
            this.store.findRecord('type', itemId).then(function (item) {
                self.store.findRecord('group', groupId).then(function (group) {
                    item.get('groups').clear();
                    item.get('groups').pushObject(group);
                    item.save().then(function () {
                        self.growl.info(item.get('name') + " moved to " + group.get('fullName'));
                    }).catch(function () {
                        item.rollback();
                    });
                });
            });
        }
    },
    activate: function() {
        $(document).attr('title', 'shelves - Part groups');
    },
    model: function () {
        return Ember.A();
    }
});
