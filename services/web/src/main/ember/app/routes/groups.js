import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    actions: {
        moveItem: function (itemId, groupId) {
            var self = this;
            console.log('drop processing');
            this.store.find('type', itemId).then(function (item) {
                self.store.find('group', groupId).then(function (group) {
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
    model: function() {
        return this.store.filter('group', function (box) {
            // return only top level boxes
            return !box.get('hasParent');
        });
    },
    activate: function() {
        $(document).attr('title', 'shelves - Part groups');
    }
});