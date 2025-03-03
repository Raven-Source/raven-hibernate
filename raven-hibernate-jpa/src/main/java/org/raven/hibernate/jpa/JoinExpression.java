package org.raven.hibernate.jpa;

import javax.persistence.criteria.Join;
import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface JoinExpression<T> extends Function<JoinBuilder<T>, List<Join<T, ?>>> {

    default Joiner<T> toJoin() {
        return (root, builder) -> apply(new JoinBuilder<T>(root, builder));
    }
}
