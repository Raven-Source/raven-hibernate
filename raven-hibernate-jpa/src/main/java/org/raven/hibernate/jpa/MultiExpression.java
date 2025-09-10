package org.raven.hibernate.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import java.util.List;

@FunctionalInterface
public interface MultiExpression<T> {

    List<Expression<?>> execute(From<?, T> from, CriteriaBuilder builder);
}
