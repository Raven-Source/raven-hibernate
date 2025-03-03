package org.raven.hibernate.util;

import lombok.NonNull;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;
import org.hibernate.query.criteria.internal.predicate.CompoundPredicate;
import org.hibernate.query.criteria.internal.predicate.InPredicate;
import org.raven.commons.util.CollectionUtils;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

/**
 * date 2022/7/26 11:40
 */
public class PredicateUtils {

    private PredicateUtils() {
    }

    public static boolean existsAttribute(Predicate predicate, @NonNull String attributeName) {

        if (predicate != null) {
            List<Expression<Boolean>> expressions = predicate.getExpressions();
            return existsAttribute(expressions, attributeName);
        }

        return false;
    }

    public static boolean existsAttribute(List<Expression<Boolean>> expressions, @NonNull String attributeName) {

        if (CollectionUtils.isNotEmpty(expressions)) {
            for (Expression<Boolean> expression : expressions) {

                Expression<?> leftExpression = null;

                if (expression instanceof ComparisonPredicate) {
                    leftExpression = ((ComparisonPredicate) expression).getLeftHandOperand();
                } else if (expression instanceof InPredicate) {
                    leftExpression = ((InPredicate<?>) expression).getExpression();
                } else if (expression instanceof CompoundPredicate) {
                    if (existsAttribute(((CompoundPredicate) expression).getExpressions(), attributeName)) {
                        return true;
                    }
                }

                if (leftExpression instanceof SingularAttributePath) {

                    SingularAttribute<?, ?> singularAttribute = ((SingularAttributePath<?>) leftExpression).getAttribute();

                    if (singularAttribute.getName().equals(attributeName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
