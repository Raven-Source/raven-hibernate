package org.raven.hibernate.util;

import lombok.NonNull;
//import org.hibernate.query.criteria.internal.path.SingularAttributePath;
//import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;
//import org.hibernate.query.criteria.internal.predicate.CompoundPredicate;
//import org.hibernate.query.criteria.internal.predicate.InPredicate;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.tree.domain.SqmPath;
import org.hibernate.query.sqm.tree.expression.SqmExpression;
import org.hibernate.query.sqm.tree.predicate.SqmComparisonPredicate;
import org.hibernate.query.sqm.tree.predicate.SqmInListPredicate;
import org.hibernate.query.sqm.tree.predicate.SqmInPredicate;
import org.hibernate.query.sqm.tree.predicate.SqmInSubQueryPredicate;
import org.raven.commons.util.CollectionUtils;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.List;

/**
 * date 2022/7/26 11:40
 */
@Slf4j
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

                SqmExpression<?> leftExpression = null;

                if (expression instanceof SqmComparisonPredicate) {
                    leftExpression = ((SqmComparisonPredicate) expression).getLeftHandExpression();
                } else if (expression instanceof SqmInListPredicate) {
                    leftExpression = ((SqmInListPredicate<?>) expression).getExpression();
                } else if (expression instanceof SqmInSubQueryPredicate) {
                    if (existsAttribute(((SqmInSubQueryPredicate<?>) expression).getExpressions(), attributeName)) {
                        return true;
                    }
                }

                if (leftExpression == null) {
                    log.error("leftExpression not get, expression class is: {}", expression.getClass().getName());
                } else if (leftExpression instanceof SqmPath<?> sqmPath) {

                    if (sqmPath.getNavigablePath().getLocalName().equals(attributeName)) {
                        return true;
                    }

//                    SingularAttribute<?, ?> singularAttribute = (SqmPath<?>) leftExpression).getn();
//
//                    if (singularAttribute.getName().equals(attributeName)) {
//                        return true;
//                    }
                }
            }
        }

        return false;
    }
}
