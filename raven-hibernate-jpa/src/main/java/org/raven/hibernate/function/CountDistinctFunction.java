package org.raven.hibernate.function;

import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.expression.function.ParameterizedFunctionExpression;
import org.raven.hibernate.dialect.SQLFunctions;

import javax.persistence.criteria.Expression;
import java.util.List;

public class CountDistinctFunction extends ParameterizedFunctionExpression<Long> {

    public static final String NAME = SQLFunctions.countDistinct;

    public CountDistinctFunction(CriteriaBuilderImpl criteriaBuilder, Expression<?>... argumentExpressions) {
        super(criteriaBuilder, Long.class, NAME, argumentExpressions);
    }

    public CountDistinctFunction(CriteriaBuilderImpl criteriaBuilder, List<Expression<?>> argumentExpressions) {
        super(criteriaBuilder, Long.class, NAME, argumentExpressions);
    }
}
