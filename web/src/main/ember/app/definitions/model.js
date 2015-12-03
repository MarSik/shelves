import Ember from 'ember';
import { attr, Model } from "ember-cli-simple-store/model";

export default Model.extend({
  // Make sure all objects are transparent for promise use
  then(f) {
    return f(this);
  },

  // Some properties might be provided later
  unknownProperty(key) {
    return Model.create();
  }
});
