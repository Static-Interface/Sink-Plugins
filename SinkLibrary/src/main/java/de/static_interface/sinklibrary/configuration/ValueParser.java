package de.static_interface.sinklibrary.configuration;

/**
 * @author Acrobot
 */
public class ValueParser {
    /**
     * Parses an object to a YAML-usable string
     *
     * @param object Object to parse
     * @return YAML string
     */
    public static String parseToYAML(Object object) {
        if (object instanceof Number || object instanceof Boolean) {
            return String.valueOf(object);
        } else {
            return '\"' + String.valueOf(object) + '\"';
        }
    }
}
