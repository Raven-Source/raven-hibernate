package org.raven.hibernate.predicate;

import lombok.Getter;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;

import javax.persistence.criteria.Expression;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

@Getter
public class ArrayHasPredicate
        extends AbstractSimplePredicate
        implements Serializable {

    private final Expression<?> expression;
    private final Expression<String> valueExpression;
    private final ArrayHasType arrayHasType;
    private final ArrayValueType arrayValueType;

    public <Y> ArrayHasPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<?> expression,
            ArrayHasType arrayHasType,
            ArrayValueType arrayValueType,
            Y... values) {
        this(criteriaBuilder, expression, arrayHasType, arrayValueType, Arrays.asList(values));
    }


    public <Y> ArrayHasPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<?> expression,
            ArrayHasType arrayHasType,
            ArrayValueType arrayValueType,
            Collection<Y> values) {
//        this(criteriaBuilder, expression, arrayHasType, valueProcess(criteriaBuilder, values));
        super(criteriaBuilder);

        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values is empty");
        }

        this.expression = expression;
        this.valueExpression = valueProcess(values);
        this.arrayHasType = arrayHasType;
        this.arrayValueType = arrayValueType;
    }

    /**
     * @param registry The parameter registry with which to register.
     */
    @Override
    public void registerParameters(ParameterRegistry registry) {
        Helper.possibleParameter(getExpression(), registry);
        Helper.possibleParameter(getValueExpression(), registry);
//        for (Expression<?> value : getValues()) {
//            Helper.possibleParameter(value, registry);
//        }

    }

    /**
     * @param isNegated        Should the predicate be negated.
     * @param renderingContext The context for rendering
     * @return The rendered predicate
     */
    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        final StringBuilder buffer = new StringBuilder();

        buffer.append("array_has(");
        buffer.append(((Renderable) getExpression()).render(renderingContext));
        buffer.append(", ");

        buffer.append(((Renderable) getValueExpression()).render(renderingContext));
        buffer.append(", ").append(arrayHasType.getValue());
        buffer.append(", ").append(arrayValueType.getValue());
        buffer.append(")");
        if (isNegated) {
            buffer.append("=0");
        } else {
            buffer.append("=1");
        }
        return buffer.toString();
    }


    private <Y> Expression<String> valueProcess(Collection<Y> values) {

        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        String sep = "";
        for (Y value : values) {
            buffer.append(sep);

            if (value instanceof CharSequence) {
                buffer.append("\"");
                buffer.append(value);
                buffer.append("\"");
            } else if (value instanceof Number) {
                buffer.append(value);
            } else {
                throw new IllegalArgumentException("Unsupported type");
            }
            sep = ", ";
        }
        buffer.append("]");

        return new LiteralExpression<>(criteriaBuilder(), buffer.toString());
    }
}
