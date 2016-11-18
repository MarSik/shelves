import Ember from 'ember';

export default Ember.Controller.extend({
  actions: {
    showCreateGroup: function (group) {
      this.set('selectedGroup', group);
      this.set('showCreateDialog', true);
    },
    showCreateTopLevelGroup: function () {
      this.set('selectedGroup', this.get('model'));
      this.set('showCreateDialog', true);
    },
    createGroup: function (name) {
      var newGroup = this.store.createRecord('group', {
        name: name,
        parent: this.get('selectedGroup')
      });
      var self = this;
      this.set('showCreateDialog', false);

      newGroup.save()
        .then(function() {
          /*if (self.get('selectedGroup')) {
           self.get('selectedGroup').get('groupes').pushObject(newGroup);
           self.store.commit();
           }*/
          self.growl.info("Group created");
        })
        .catch(function() {
          newGroup.rollbackAttributes();
          self.growl.error("Group creation failed");
        });
    },
        addProperty: function (group, property) {
            group.get('showProperties').pushObject(property);
            var self = this;
            group.save().catch(function (e) {
                group.rollbackAttributes();
                self.growl.error("Could not add property: "+e);
            });
        },
        removeProperty: function (group, property) {
            group.get('showProperties').removeObject(property);
            var self = this;
            group.save().catch(function (e) {
                group.rollbackAttributes();
                self.growl.error("Could not remove property: "+e);
            });
        },
        sortBy: function (p, desc) {
            var key = p;
            if (desc) {
                key += ':desc';
            }
            this.set('typeSorting', [key]);
        },
        sortByProperty: function (property, desc) {
            var key = 'values.'+property.get('id');
            if (desc) {
                key += ':desc';
            }
            this.set('typeSorting', [key]);
        },
        manageProperties: function (value) {
            this.set('manageProperties', value);
        }
    },
    
    application: Ember.inject.controller("application"),
    propSorting: ['name'],
    sortedProperties: Ember.computed.sort('application.availableProperties', 'propSorting'),
    typeColumns: function () {
        return 4 + this.get('model.showProperties.length');
    }.property('model.showProperties.size'),

    typeSorting: ['name'],
    sortedTypes: Ember.computed.sort('model.types', 'typeSorting'),

    _categories: [],
    categoriesList: Ember.computed('_categories', 'model.groups', {
      get() {
        if (!Ember.isEmpty(this.get('_categories'))
          && this.get('_categories') == this.get('model')) {
          console.log('CACHED');
          return this.get('_categories');
        }

        var self = this;
        this.set('_categories', []);

        var f = function (group) {
          if (!Ember.isEmpty(group.get('types'))) {
            self.get('_categories').pushObject(group);
          }
          console.log('ADDING ', group);

          group.get('groups').then(function (grps) {
            grps.forEach(function (g) {
              console.log("Nested " + g);
              f(g);
            });
          });
        };

        f(this.get('model'));
        return this.get('_categories');
      },
      set(k, v) {
        this.set('_categories', v);
      }
    }),

    categorySorting: ['fullName'],
    sortedCategoriesList: Ember.computed.sort('categoriesList', 'categorySorting')
});
