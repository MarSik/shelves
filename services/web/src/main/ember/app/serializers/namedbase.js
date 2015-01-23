import DS from 'ember-data';
import AppSerializer from './application';

export default AppSerializer.extend(DS.EmbeddedRecordsMixin, {
    //Force embedding the values array into the payload to the server
    attrs: {
        values: {
            serialize: 'records'
        }
    }
});
