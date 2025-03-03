package org.raven.hibernate.jpa;

import javax.persistence.criteria.Expression;
import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface GroupByExpression<T> extends Function<GroupByBuilder<?, T>, List<Expression<?>>> {
    default GroupBy<T> toGroupBy() {
        return (root, builder) -> apply(new GroupByBuilder<>(root, builder));
    }
}
