import DS from 'ember-data';

var attr = DS.attr,
    belongsTo = DS.belongsTo;

export default DS.Model.extend({
    name: DS.attr('string'),
    summary: DS.attr('string'),
    description: DS.attr('string'),
    url: DS.attr('string'),
    belongsTo: belongsTo('user'),
    nameWithDesc: function () {
        var n = this.get('name');
        if (this.get('summary')) {
            n += " | " + this.get('summary');
        }
        return n;
    }.property('name', 'summary')
});
