package org.raven.hibernate.jpa;

import javax.persistence.criteria.Order;
import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface SorterExpression<T> extends Function<SorterBuilder<?, T>, List<Order>> {
    default Sorter<T> toSorter() {
        return (root, builder) -> apply(new SorterBuilder<>(root, builder));
    }
}
