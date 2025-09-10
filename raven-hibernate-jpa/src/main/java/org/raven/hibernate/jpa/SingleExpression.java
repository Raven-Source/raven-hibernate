package org.raven.hibernate.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Root;

/**
 * date 2022/10/26 15:14
 */
@FunctionalInterface
public interface SingleExpression<T, R> {

    default Expression<R> execute(Root<T> root, CriteriaBuilder builder) {
        return this.execute((From<T, T>) root, builder);
    }

    Expression<R> execute(From<?, T> from, CriteriaBuilder builder);

}
