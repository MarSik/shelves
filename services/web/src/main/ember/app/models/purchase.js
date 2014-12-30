import DS from 'ember-data';
import Lot from './lot';

var attr = DS.attr,
    hasMany = DS.hasMany,
    belongsTo = DS.belongsTo;

export default Lot.extend({
  singlePrice: attr("number"),
  totalPrice: attr("number"),
  vat: attr("number"),
  vatIncluded: attr("boolean"),
  transaction: belongsTo("transaction")
});
