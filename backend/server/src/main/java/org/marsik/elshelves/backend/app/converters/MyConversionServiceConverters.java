package org.marsik.elshelves.backend.app.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;

import java.util.TimeZone;

public class MyConversionServiceConverters {
    static public void registerConverters(ConverterRegistry registry) {
        registry.addConverter(new Converter<TimeZone, String>() {
            @Override
            public String convert(TimeZone source) {
                return source.getID();
            }
        });
        registry.addConverter(new Converter<String, TimeZone>() {
            @Override
            public TimeZone convert(String source) {
                return TimeZone.getTimeZone(source);
            }
        });
    }
}
