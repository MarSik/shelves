import Ember from 'ember';

export default Ember.Component.extend({
    actions: {
        removeRow: function () {
            this.sendAction("removeRow", this.get("purchase"));
        },
        createType: function (name) {
            this.sendAction("createType", name);
        },
        save: function () {
            this.sendAction("save", this.get("purchase"));
        }
    },
    tagName: "tbody"

    // purchase
    // sortedTypes
    //
});