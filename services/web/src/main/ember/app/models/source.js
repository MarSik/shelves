import DS from 'ember-data';
import NamedBase from './namedbase';

var attr = DS.attr,
    belongsTo = DS.belongsTo;

export default NamedBase.extend({
    url: DS.attr('string'),
    nameWithDesc: function () {
        var n = this.get('name');
        if (this.get('summary')) {
            n += " | " + this.get('summary');
        }
        return n;
    }.property('name', 'summary')
});
