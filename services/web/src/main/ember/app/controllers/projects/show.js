import Ember from 'ember';

export default Ember.Controller.extend({
    needs: "application",
    actions: {
        showAddAlternative: function (req) {
            this.set('lastRequirement', req);
            $("#addAlternative").foundation("reveal", "open");
        },
        addAlternativePart: function () {
            $("#addAlternative").foundation("reveal", "close");
            return true;
        }
    },
    typeSorting: ['name'],
    sortedTypes: Ember.computed.sort('controllers.application.availableTypes', 'typeSorting'),
    requiredType: null,
    requiredCount: 1,
    lastRequirement: null,
    alternativeType: null,
    submitDisabled: function() {
        return this.get('requiredType') == null || this.get('requiredCount') == null || this.get('requiredCount') < 1;
    }.property('requiredType', 'requiredCount')
});
