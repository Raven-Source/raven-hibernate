package org.raven.hibernate.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import java.util.List;

@FunctionalInterface
public interface MultiExpression<T> {

    List<Expression<?>> execute(From<?, T> from, CriteriaBuilder builder);
}
