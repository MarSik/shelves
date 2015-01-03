import Ember from 'ember';

export default Ember.Controller.extend({
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
    sortedTypes: Ember.computed.sort('types', 'typeSorting'),
    requiredType: null,
    requiredCount: 1,
    lastRequirement: null,
    alternativeType: null
});
