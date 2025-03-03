package org.raven.hibernate.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import java.util.List;

@FunctionalInterface
public interface GroupBy<T> extends MultiExpression<T> {
    List<Expression<?>> grouping(From<?, T> from, CriteriaBuilder builder);

    @Override
    default List<Expression<?>> execute(From<?, T> from, CriteriaBuilder builder) {
        return grouping(from, builder);
    }
}
