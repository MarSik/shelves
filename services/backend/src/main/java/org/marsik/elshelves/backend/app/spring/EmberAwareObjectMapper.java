package org.marsik.elshelves.backend.app.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON serialization configuration for Ember.js frontend
 * based on: http://springember.blogspot.cz/2014/08/using-ember-data-restadapter-with.html
 */
public class EmberAwareObjectMapper extends ObjectMapper {

    public EmberAwareObjectMapper() {
        // Indent the json output so it is easier to read
        configure(SerializationFeature.INDENT_OUTPUT, true);

        // Write/Read dates as ISO Strings
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Use equals to check for object equality
        configure(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID, true);

        // Throw exceptions
        configure(SerializationFeature.WRAP_EXCEPTIONS, false);
        configure(DeserializationFeature.WRAP_EXCEPTIONS, false);
    }
}