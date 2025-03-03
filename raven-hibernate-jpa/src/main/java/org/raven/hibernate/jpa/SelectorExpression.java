package org.raven.hibernate.jpa;

import javax.persistence.criteria.Selection;
import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface SelectorExpression<T> extends Function<SelectorBuilder<?, T>, List<Selection<?>>> {
    default Selector<T> toSelector() {
        return (from, builder) -> apply(new SelectorBuilder<>(from, builder));
    }
}
