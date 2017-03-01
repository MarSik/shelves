import Ember from 'ember';
// global Promise
// global d3

export default Ember.Component.extend({
  tagName: "",

  data: Ember.computed("unused", "usedInPast", "used", function () {
    return {columns: [["new", this.get("unused")],
                      ["assigned", this.get("reserved")],
                      ["used in past", this.get('usedInPast')],
                      ["used", this.get("used")]],
            type: 'donut',
            colors: {"new": 'green', "assigned": "blue", "used in past": 'orange', "used": 'black'}};
  }),

  meta: Ember.computed("title", function () {
    return { title: this.get("title"), label: {show: false} };
  })
});
