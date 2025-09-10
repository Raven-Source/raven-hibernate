package org.raven.hibernate.util;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import java.util.HashMap;
import java.util.Map;

public class TupleUtils {

    private TupleUtils() {
    }

    public static Map<String, Object> tupleToMap(final Tuple tuple) {

        Map<String, Object> map = new HashMap<>();
        for (TupleElement<?> element : tuple.getElements()) {
            map.put(element.getAlias(), tuple.get(element.getAlias()));
        }

        return map;
    }
}
