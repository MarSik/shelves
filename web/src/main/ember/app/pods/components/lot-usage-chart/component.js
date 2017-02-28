import Ember from 'ember';
// global Promise

export default Ember.Component.extend({
  tagName: "",

  data: Ember.computed("unused", "usedInPast", "used", function () {
    return {columns: [["new", this.get("unused")],
                      ["used in past", this.get('usedInPast')],
                      ["used", this.get("used")]],
            type: 'donut',
            colors: {"new": 'green', "used in past": 'orange', "used": 'black'}};
  }),

  meta: Ember.computed("title", function () {
    return {title: this.get("title")};
  }),
});
