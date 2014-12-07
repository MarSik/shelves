package org.marsik.elshelves.backend.app.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON serialization configuration for Ember.js frontend
 * based on: http://springember.blogspot.cz/2014/08/using-ember-data-restadapter-with.html
 */
public class EmberAwareObjectMapper extends ObjectMapper {

    public EmberAwareObjectMapper() {
        //indent the json output so it is easier to read
        configure(SerializationFeature.INDENT_OUTPUT, true);

        //Wite/Read dates as ISO Strings
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

}