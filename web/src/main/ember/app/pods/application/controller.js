import Ember from 'ember';

export default Ember.Controller.extend({
  codeTypes: [
    {id: "UPC_A", name: "UPC-A"},
    {id: "UPC_E", name: "UPC-E"},
    {id: "EAN_8", name: "EAN-8"},
    {id: "EAN_13", name: "EAN-13"},
    {id: "CODE_39", name: "Code 39"},
    {id: "CODE_93", name: "Code 93"},
    {id: "CODE_128", name: "Code 128"},
    {id: "CODABAR", name: "Codabar"},
    {id: "ITF", name: "ITF"},
    {id: "RSS_14", name: "RSS-14"},
    {id: "RSS_EXPANDED", name: "RSS-Expanded"},
    {id: "QR_CODE", name: "QR Code"},
    {id: "DATA_MATRIX", name: "Data Matrix"},
    {id: "AZTEC", name: "Aztec"},
    {id: "PDF_417", name: "PDF 417"}
  ]
});
