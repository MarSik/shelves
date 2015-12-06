package org.marsik.elshelves.ember;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.atteo.evo.inflector.English;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EmberModelHelper {
    public static LinkedList<String> splitCamelCaseString(String s){
        LinkedList<String> result = new LinkedList<>();
        Collections.addAll(result, s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"));
        return result;
    }

    public static String toLowerCamel(String name) {
        List<String> parts = splitCamelCaseString(name);
        StringBuilder result = new StringBuilder();

        for (String part: parts) {
            if (result.length() == 0) {
                result.append(part.toLowerCase());
            } else {
                result.append(String.valueOf(part.charAt(0)).toUpperCase());
                result.append(part.substring(1).toLowerCase());
            }
        }

        return result.toString();
    }

    public static String getSingularName(final Class<?> clazz) {
        if (clazz.isAnnotationPresent(JsonTypeName.class)) {
            return clazz.getAnnotation(JsonTypeName.class).value();
        }
        else {
            return toLowerCamel(clazz.getSimpleName());
        }
    }

    public static String getPluralName(final Class<?> clazz) {
        return English.plural(getSingularName(clazz));
    }

    public static String getPluralName(String singular) {
        return English.plural(singular);
    }
}
