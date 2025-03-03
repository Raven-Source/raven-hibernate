package org.raven.hibernate.jpa;

import javax.persistence.criteria.Expression;
import java.util.function.Function;

@FunctionalInterface
public interface FilterExpression<T> extends Function<FilterBuilder<T>, Expression<Boolean>> {

    default Filter<T> toFilter() {
        return (root, builder) -> apply(new FilterBuilder<>(root, builder));
    }
}
