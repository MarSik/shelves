import DS from 'ember-data';
import AppSerializer from './application';

export default AppSerializer.extend(DS.EmbeddedRecordsMixin, {
    //Force embedding the items array into the payload to the server
    attrs: {
        items: {
            serialize: 'records'
        }
    }
});
