import Ember from 'ember';
import computedIndirect from 'ember-computed-indirect/utils/indirect';

export default {
    name: 'ms-ember-computed-indirect',
    initialize: function() {
        Ember.computed.indirect = computedIndirect;
    }
};
