import Ember from 'ember';

export default Ember.Controller.extend({
    possibleTypes: Ember.computed.map('model.type', m => m),
    assignedLots: Ember.computed.map('model.lots', m => m),
    missing: Ember.computed.alias('model.missing'),
    count: Ember.computed.alias('model.count'),

    candidateLots: function () {
        var req = this.get('model');

        console.log('Recomputing available parts');

        var usable = Ember.A();

        if (Ember.isEmpty(req)) {
            return usable;
        }

        this.get('possibleTypes').forEach(function (type) {
            console.log('type:');
            console.log(type);
            type.get('lots').then(function (ll) {
                ll.forEach(function (lot) {
                    console.log(lot);
                    usable.pushObject(lot);
                });
            });
        });

        console.log("done");
        console.log(usable);

        return usable;
    }.property('possibleTypes.@each.lots'),

    assignableLots: Ember.computed.map('resolvedLots', m => m),
    resolvedLots: Ember.computed.filterBy('candidateLots', 'canBeAssigned', true)
});