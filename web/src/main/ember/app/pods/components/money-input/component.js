import Ember from 'ember';

export default Ember.TextField.extend({
    type: 'text',
    attributeBindings: ['pattern'],
    pattern: '[0-9]*[.,]?[0-9]+',
    min: 0,
    max: undefined,
    step: 'any',

    // Make sure all prices use decimal dot (and not comma)
    keyDown: function(e) {
      if (e.keyCode == 188 || e.keyCode == 190) {
        var val = this.get('value');
        if (!val.includes('.')) this.set('value', val + '.');
        e.preventDefault();
      }
    }
});
