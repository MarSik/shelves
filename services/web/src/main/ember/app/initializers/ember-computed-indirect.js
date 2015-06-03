import Ember from 'ember';

var computedIndirect = function (middlePropertyName) {
    return function(key, value) {
        var sourceProperty = this.get(middlePropertyName);

        var lastSourceProperty = "_indirect_" + key + "_lastSourceProperty";
        var sourcePropertyObserver = "_indirect_" + key + "_sourcePropertyObserver";

        if (this[lastSourceProperty] !== sourceProperty) {
            if (this[lastSourceProperty] && this[sourcePropertyObserver]) {
                this.removeObserver(this[lastSourceProperty], this, this[sourcePropertyObserver]);
            }

            this[sourcePropertyObserver] = function() {
                this.notifyPropertyChange(key);
            };

            this.addObserver(sourceProperty, this, sourcePropertyObserver);
            this[lastSourceProperty] = sourceProperty;
        }

        if (arguments.length > 1) {
            this.set(sourceProperty, value);
        }

        return this.get(sourceProperty);
    }.property(middlePropertyName);
};

export default {
    name: 'ember-computed-indirect',
    initialize: function() {
        Ember.computed.indirect = computedIndirect;
    }
};
