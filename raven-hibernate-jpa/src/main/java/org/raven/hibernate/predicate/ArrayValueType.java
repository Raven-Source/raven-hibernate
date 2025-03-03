package org.raven.hibernate.predicate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArrayValueType {

    STRING_ARRAY(1),
    INT8_ARRAY(10),
    INT16_ARRAY(11),
    INT32_ARRAY(12),
    INT64_ARRAY(13),
    ;

    private final int value;

    public static ArrayValueType judgment(Class<?> type) {

        if (type == String.class) {
            return ArrayValueType.STRING_ARRAY;
        } else if (type == Byte.class || type == byte.class) {
            return ArrayValueType.INT8_ARRAY;
        } else if (type == Short.class || type == short.class) {
            return ArrayValueType.INT16_ARRAY;
        } else if (type == Integer.class || type == int.class) {
            return ArrayValueType.INT32_ARRAY;
        } else if (type == Long.class || type == long.class) {
            return ArrayValueType.INT64_ARRAY;
        }

        return null;

    }

    public static ArrayValueType of(int value) {
        for (ArrayValueType item : values()) {
            if (item.value == value) {
                return item;
            }
        }
        return null;
    }
}
