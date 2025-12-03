package org.raven.hibernate.util;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling JPA Tuple related operations
 */
public class TupleUtils {

    private TupleUtils() {
    }

    /**
     * Converts a Tuple object to {@code Map<String, Object>} format
     * 
     * @param tuple the Tuple object to convert
     * @return a Map containing all elements from the Tuple, with aliases as keys and corresponding values
     */
    public static Map<String, Object> tupleToMap(final Tuple tuple) {

        Map<String, Object> map = new HashMap<>();
        for (TupleElement<?> element : tuple.getElements()) {
            map.put(element.getAlias(), tuple.get(element.getAlias()));
        }

        return map;
    }
}