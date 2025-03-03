package org.raven.hibernate.predicate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArrayHasType {

    ALL(0),
    ANY(1),
    ;

    private final int value;

    public static ArrayHasType of(int value) {
        for (ArrayHasType item : values()) {
            if (item.value == value) {
                return item;
            }
        }
        return null;
    }

}
