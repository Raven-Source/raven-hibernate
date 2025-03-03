package org.raven.hibernate.jpa;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.raven.hibernate.predicate.ArrayHasPredicate;
import org.raven.hibernate.predicate.ArrayHasType;
import org.raven.hibernate.predicate.ArrayValueType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author by yanfengf
 * date 2021/9/13 20:00
 */
@SuppressWarnings("unchecked")
public abstract class PredicateBuilder<S, T, P extends PredicateBuilder<S, T, P>> extends AbstractBuilder<S, T> {

    private List<Predicate> predicates = new ArrayList<>();

    @Getter
    @Setter
    private Predicate.BooleanOperator booleanOperator;

    public PredicateBuilder(From<S, T> from, CriteriaBuilder builder) {
        this(from, builder, Predicate.BooleanOperator.AND);
    }

    public PredicateBuilder(From<S, T> from, CriteriaBuilder builder, Predicate.BooleanOperator booleanOperator) {
        super(from, builder);
        this.booleanOperator = booleanOperator;
    }

//    public FilterBuilder(Class<S, T> entityType, CriteriaBuilder builder) {
//        this(entityType, builder, Predicate.BooleanOperator.AND);
//    }
//
//    public FilterBuilder(Class<S, T> entityType, CriteriaBuilder builder, Predicate.BooleanOperator booleanOperator) {
//        super(entityType, builder);
//        this.booleanOperator = booleanOperator;
//    }

//    public static <S, T> PredicateBuilder<S, T> empty(From<S, T> from, CriteriaBuilder builder) {
//        return empty(from, builder, Predicate.BooleanOperator.AND);
//    }
//
//    public static <S, T> PredicateBuilder<S, T> empty(From<S, T> from, CriteriaBuilder builder, Predicate.BooleanOperator booleanOperator) {
//        return new PredicateBuilder<>(from, builder, booleanOperator);
//    }

//    public static <S, T> FilterBuilder<S, T> empty(Class<S, T> entityType, CriteriaBuilder builder) {
//        return empty(entityType, builder, Predicate.BooleanOperator.AND);
//    }

//    public static <S, T> FilterBuilder<S, T> empty(Class<S, T> entityType, CriteriaBuilder builder, Predicate.BooleanOperator booleanOperator) {
//        return new FilterBuilder<>(entityType, builder, booleanOperator);
//    }

    public boolean isEmpty() {
        return predicates.isEmpty();
    }

    //region 为空/非空

    /**
     * 为空
     *
     * @param attributeName 字段
     * @return this
     */
    public P isNull(String attributeName) {
        return isNull(getAttribute(attributeName));
    }

    /**
     * 为空
     *
     * @param attribute 字段
     * @param <R>       the type of the expression
     * @return this
     */
    public <R> P isNull(SingleExpression<T, R> attribute) {
        return isNull(attribute.execute(from, builder));
    }

    /**
     * 为空
     *
     * @param attribute 字段
     * @return this
     */
    public P isNull(Expression<?> attribute) {
        predicates.add(
                builder.isNull(attribute)
        );
        return (P) this;
    }

    /**
     * 非空
     *
     * @param attributeName 字段
     * @return this
     */
    public P isNotNull(String attributeName) {
        return isNotNull(getAttribute(attributeName));
    }

    /**
     * 非空
     *
     * @param attribute 字段
     * @param <R>       the type of the expression
     * @return this
     */
    public <R> P isNotNull(SingleExpression<T, R> attribute) {
        return isNotNull(attribute.execute(from, builder));
    }

    /**
     * 非空
     *
     * @param attribute 字段
     * @return this
     */
    public P isNotNull(Expression<?> attribute) {
        predicates.add(
                builder.isNotNull(attribute)
        );
        return (P) this;
    }

    //endregion

    //region 等于

    /**
     * 等于
     *
     * @param attributeName 字段
     * @param value         参数
     * @return this
     */
    public P equal(String attributeName, Object value) {
        return equal(getAttribute(attributeName), value);
    }

    /**
     * 等于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <R>       the type of the expression
     * @return this
     */
    public <R> P equal(SingleExpression<T, R> attribute, Object value) {
        return equal(attribute.execute(from, builder), value);
    }

    /**
     * 等于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P equal(Expression<?> attribute, Object value) {
        predicates.add(
                builder.equal(attribute, value)
        );
        return (P) this;
    }

    /**
     * 等于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P equal(Expression<?> attribute, Expression<?> value) {
        predicates.add(
                builder.equal(attribute, value)
        );
        return (P) this;
    }

    //endregion

    //region 不等于

    /**
     * 不等于
     *
     * @param attributeName 字段
     * @param value         参数
     * @return this
     */
    public P notEqual(String attributeName, Object value) {
        return notEqual(getAttribute(attributeName), value);
    }

    /**
     * 不等于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <R>       the type of the expression
     * @return this
     */
    public <R> P notEqual(SingleExpression<T, R> attribute, Object value) {
        return notEqual(attribute.execute(from, builder), value);
    }

    /**
     * 不等于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P notEqual(Expression<?> attribute, Object value) {
        predicates.add(
                builder.notEqual(attribute, value)
        );
        return (P) this;
    }

    /**
     * 不等于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P notEqual(Expression<?> attribute, Expression<?> value) {
        predicates.add(
                builder.notEqual(attribute, value)
        );
        return (P) this;
    }

    //endregion

    //region 大于、小于

    /**
     * 大于
     *
     * @param attributeName 字段
     * @param value         参数
     * @return this
     */
    public P gt(String attributeName, Number value) {
        return gt(getAttribute(attributeName), value);
    }

    /**
     * 大于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P gt(SingleExpression<T, ? extends Number> attribute,
                Number value) {
        return gt(attribute.execute(from, builder), value);
    }

    /**
     * 大于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P gt(Expression<? extends Number> attribute,
                Number value) {
        predicates.add(
                builder.gt(attribute, value)
        );
        return (P) this;
    }

    /**
     * 大于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P gt(Expression<? extends Number> attribute,
                Expression<? extends Number> value) {
        predicates.add(
                builder.gt(attribute, value)
        );
        return (P) this;
    }

    /**
     * 大于等于
     *
     * @param attributeName 字段
     * @param value         参数
     * @return this
     */
    public P ge(String attributeName, Number value) {
        return ge(getAttribute(attributeName), value);
    }

    /**
     * 大于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P ge(SingleExpression<T, ? extends Number> attribute,
                Number value) {
        return ge(attribute.execute(from, builder), value);
    }

    /**
     * 大于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P ge(Expression<? extends Number> attribute,
                Number value) {
        predicates.add(
                builder.ge(attribute, value)
        );
        return (P) this;
    }

    /**
     * 大于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P ge(Expression<? extends Number> attribute,
                Expression<? extends Number> value) {
        predicates.add(
                builder.ge(attribute, value)
        );
        return (P) this;
    }

    /**
     * 小于
     *
     * @param attributeName 字段
     * @param value         参数
     * @return this
     */
    public P lt(String attributeName, Number value) {
        return lt(getAttribute(attributeName), value);
    }

    /**
     * 小于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P lt(SingleExpression<T, ? extends Number> attribute,
                Number value) {
        return lt(attribute.execute(from, builder), value);
    }

    /**
     * 小于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P lt(Expression<? extends Number> attribute,
                Number value) {
        predicates.add(
                builder.lt(attribute, value)
        );
        return (P) this;
    }

    /**
     * 小于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P lt(Expression<? extends Number> attribute,
                Expression<? extends Number> value) {
        predicates.add(
                builder.lt(attribute, value)
        );
        return (P) this;
    }

    /**
     * 小于等于
     *
     * @param attributeName 字段
     * @param value         参数
     * @return this
     */
    public P le(String attributeName, Number value) {
        return le(getAttribute(attributeName), value);
    }

    /**
     * 小于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P le(SingleExpression<T, ? extends Number> attribute,
                Number value) {
        return le(attribute.execute(from, builder), value);
    }

    /**
     * 小于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P le(Expression<? extends Number> attribute,
                Number value) {
        predicates.add(
                builder.le(attribute, value)
        );
        return (P) this;
    }

    /**
     * 小于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P le(Expression<? extends Number> attribute,
                Expression<? extends Number> value) {
        predicates.add(
                builder.le(attribute, value)
        );
        return (P) this;
    }

    /**
     * 大于
     *
     * @param attributeName 字段
     * @param value         参数
     * @param <Y>           the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P greaterThan(String attributeName, Y value) {
        return greaterThan(getAttribute(attributeName), value);
    }

    /**
     * 大于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P greaterThan(SingleExpression<T, ? extends Y> attribute,
                                                           Y value) {
        return greaterThan(attribute.execute(from, builder), value);
    }

    /**
     * 大于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P greaterThan(Expression<? extends Y> attribute, Y value) {
        predicates.add(
                builder.greaterThan(attribute, value)
        );
        return (P) this;
    }

    /**
     * 大于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P greaterThan(SingleExpression<T, ? extends Y> attribute,
                                                           SingleExpression<T, ? extends Y> value) {
        return greaterThan(attribute.execute(from, builder), value.execute(from, builder));
    }

    /**
     * 大于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P greaterThan(Expression<? extends Y> attribute,
                                                           Expression<? extends Y> value) {
        predicates.add(
                builder.greaterThan(attribute, value)
        );
        return (P) this;
    }

    /**
     * 大于等于
     *
     * @param attributeName 字段
     * @param value         参数
     * @param <Y>           the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P greaterThanOrEqualTo(String attributeName, Y value) {
        return greaterThanOrEqualTo(getAttribute(attributeName), value);
    }

    /**
     * 大于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P greaterThanOrEqualTo(SingleExpression<T, ? extends Y> attribute,
                                                                    Y value) {
        return greaterThanOrEqualTo(attribute.execute(from, builder), value);
    }

    /**
     * 大于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P greaterThanOrEqualTo(Expression<? extends Y> attribute, Y value) {
        predicates.add(
                builder.greaterThanOrEqualTo(attribute, value)
        );
        return (P) this;
    }

    /**
     * 大于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P greaterThanOrEqualTo(SingleExpression<T, ? extends Y> attribute,
                                                                    SingleExpression<T, ? extends Y> value) {
        return greaterThanOrEqualTo(attribute.execute(from, builder), value.execute(from, builder));
    }

    /**
     * 大于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P greaterThanOrEqualTo(Expression<? extends Y> attribute,
                                                                    Expression<? extends Y> value) {
        predicates.add(
                builder.greaterThanOrEqualTo(attribute, value)
        );
        return (P) this;
    }

    /**
     * 小于
     *
     * @param attributeName 字段
     * @param value         参数
     * @param <Y>           the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P lessThan(String attributeName, Y value) {
        return lessThan(getAttribute(attributeName), value);
    }

    /**
     * 小于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P lessThan(SingleExpression<T, ? extends Y> attribute,
                                                        Y value) {
        return lessThan(attribute.execute(from, builder), value);
    }

    /**
     * 小于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P lessThan(Expression<? extends Y> attribute, Y value) {
        predicates.add(
                builder.lessThan(attribute, value)
        );
        return (P) this;
    }

    /**
     * 小于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P lessThan(SingleExpression<T, ? extends Y> attribute,
                                                        SingleExpression<T, ? extends Y> value) {
        return lessThan(attribute.execute(from, builder), value.execute(from, builder));
    }

    /**
     * 小于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P lessThan(Expression<? extends Y> attribute,
                                                        Expression<? extends Y> value) {
        predicates.add(
                builder.lessThan(attribute, value)
        );
        return (P) this;
    }

    /**
     * 小于等于
     *
     * @param attributeName 字段
     * @param value         参数
     * @param <Y>           the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P lessThanOrEqualTo(String attributeName, Y value) {
        return lessThanOrEqualTo(getAttribute(attributeName), value);
    }

    /**
     * 小于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P lessThanOrEqualTo(SingleExpression<T, ? extends Y> attribute,
                                                                 Y value) {
        return lessThanOrEqualTo(attribute.execute(from, builder), value);
    }

    /**
     * 小于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P lessThanOrEqualTo(Expression<? extends Y> attribute, Y value) {
        predicates.add(
                builder.lessThanOrEqualTo(attribute, value)
        );
        return (P) this;
    }

    /**
     * 小于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P lessThanOrEqualTo(SingleExpression<T, ? extends Y> attribute,
                                                                 SingleExpression<T, ? extends Y> value) {
        return lessThanOrEqualTo(attribute.execute(from, builder), value.execute(from, builder));
    }

    /**
     * 小于等于
     *
     * @param attribute 字段
     * @param value     参数
     * @param <Y>       the type of the value
     * @return this
     */
    public <Y extends Comparable<? super Y>> P lessThanOrEqualTo(Expression<? extends Y> attribute,
                                                                 Expression<? extends Y> value) {
        predicates.add(
                builder.lessThanOrEqualTo(attribute, value)
        );
        return (P) this;
    }

    //endregion

    //region in

    /**
     * 等于
     *
     * @param attributeName 字段
     * @param values        参数
     * @return this
     */
    public P in(String attributeName, Collection<?> values) {

        return in(getAttribute(attributeName), values);
    }

    /**
     * 等于
     *
     * @param attribute 字段
     * @param values    参数
     * @param <R>       the type of the expression
     * @return this
     */
    public <R> P in(SingleExpression<T, R> attribute, Collection<?> values) {

        return in(attribute.execute(from, builder), values);
    }

    /**
     * 等于
     *
     * @param attribute 字段
     * @param values    参数
     * @return this
     */
    public P in(Expression<?> attribute, Collection<?> values) {

        CriteriaBuilder.In<Object> in = builder.in(attribute);
        for (Object o : values) {
            in.value(o);
        }
        predicates.add(in);
        return (P) this;
    }

    /**
     * 等于
     *
     * @param attributeName 字段
     * @param values        参数
     * @return this
     */
    public P notIn(String attributeName, Collection<?> values) {

        return notIn(getAttribute(attributeName), values);
    }

    /**
     * 等于
     *
     * @param attribute 字段
     * @param values    参数
     * @param <R>       the type of the expression
     * @return this
     */
    public <R> P notIn(SingleExpression<T, R> attribute, Collection<?> values) {

        return notIn(attribute.execute(from, builder), values);
    }

    /**
     * 等于
     *
     * @param attribute 字段
     * @param values    参数
     * @return this
     */
    public P notIn(Expression<?> attribute, Collection<?> values) {

        CriteriaBuilder.In<Object> in = builder.in(attribute);
        for (Object o : values) {
            in.value(o);
        }
        predicates.add(builder.not(in));
        return (P) this;
    }

    //endregion

    //region arrayHas

    /**
     * 数组包含
     *
     * @param attributeName  字段
     * @param arrayHasType   {@link  ArrayHasType}
     * @param arrayValueType {@link  ArrayValueType}
     * @param values         参数
     * @return this
     */
    public P arrayHas(String attributeName,
                      ArrayHasType arrayHasType,
                      ArrayValueType arrayValueType,
                      Collection<?> values) {
        return arrayHas(getAttribute(attributeName), arrayHasType, arrayValueType, values);
    }

    /**
     * 数组包含
     *
     * @param attribute      字段
     * @param arrayHasType   {@link  ArrayHasType}
     * @param arrayValueType {@link  ArrayValueType}
     * @param values         参数
     * @param <R>            the type of the expression
     * @return this
     */
    public <R> P arrayHas(SingleExpression<T, R> attribute,
                          ArrayHasType arrayHasType,
                          ArrayValueType arrayValueType,
                          Collection<?> values) {

        return arrayHas(attribute.execute(from, builder), arrayHasType, arrayValueType, values);
    }

    /**
     * 数组包含
     *
     * @param attribute      字段
     * @param arrayHasType   {@link  ArrayHasType}
     * @param arrayValueType {@link  ArrayValueType}
     * @param values         参数
     * @return this
     */
    public P arrayHas(Expression<?> attribute,
                      ArrayHasType arrayHasType,
                      ArrayValueType arrayValueType,
                      Collection<?> values) {

        ArrayHasPredicate has = new ArrayHasPredicate(
                (CriteriaBuilderImpl) builder, attribute, arrayHasType, arrayValueType, values
        );
        predicates.add(has);
        return (P) this;
    }

    /**
     * 数组包含
     *
     * @param attributeName  字段
     * @param arrayValueType {@link  ArrayValueType}
     * @param values         参数
     * @return this
     */
    public P arrayHasAny(String attributeName,
                         ArrayValueType arrayValueType,
                         Collection<?> values) {
        return arrayHas(attributeName, ArrayHasType.ANY, arrayValueType, values);
    }

    /**
     * 数组包含
     *
     * @param attribute      字段
     * @param arrayValueType {@link  ArrayValueType}
     * @param values         参数
     * @param <R>            the type of the expression
     * @return this
     */
    public <R> P arrayHasAny(SingleExpression<T, R> attribute,
                             ArrayValueType arrayValueType,
                             Collection<?> values) {
        return arrayHas(attribute, ArrayHasType.ANY, arrayValueType, values);
    }

    /**
     * 数组包含
     *
     * @param attribute      字段
     * @param arrayValueType {@link  ArrayValueType}
     * @param values         参数
     * @return this
     */
    public P arrayHasAny(Expression<?> attribute,
                         ArrayValueType arrayValueType,
                         Collection<?> values) {
        return arrayHas(attribute, ArrayHasType.ANY, arrayValueType, values);
    }

    /**
     * 数组包含
     *
     * @param attributeName  字段
     * @param arrayValueType {@link  ArrayValueType}
     * @param values         参数
     * @return this
     */
    public P arrayHasAll(String attributeName,
                         ArrayValueType arrayValueType,
                         Collection<?> values) {

        return arrayHas(attributeName, ArrayHasType.ALL, arrayValueType, values);
    }

    /**
     * 数组包含
     *
     * @param attribute      字段
     * @param arrayValueType {@link  ArrayValueType}
     * @param values         参数
     * @param <R>            the type of the expression
     * @return this
     */
    public <R> P arrayHasAll(SingleExpression<T, R> attribute,
                             ArrayValueType arrayValueType,
                             Collection<?> values) {
        return arrayHas(attribute, ArrayHasType.ALL, arrayValueType, values);
    }

    /**
     * 数组包含
     *
     * @param attribute      字段
     * @param arrayValueType {@link  ArrayValueType}
     * @param values         参数
     * @return this
     */
    public P arrayHasAll(Expression<?> attribute,
                         ArrayValueType arrayValueType,
                         Collection<?> values) {
        return arrayHas(attribute, ArrayHasType.ALL, arrayValueType, values);
    }

    //endregion

    //region like

    /**
     * like
     *
     * @param attributeName 字段
     * @param value         参数
     * @return this
     */
    public P like(String attributeName, Object value) {
        return like(getAttribute(attributeName), value);
    }

    /**
     * like
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P like(SingleExpression<T, String> attribute, Object value) {
        return like(attribute.execute(from, builder), value);
    }

    /**
     * like
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P like(Expression<String> attribute, Object value) {
        predicates.add(
                builder.like(attribute, "%" + value + "%")
        );
        return (P) this;
    }

    //endregion

    //region likeRight

    /**
     * likeRight
     *
     * @param attributeName 字段
     * @param value         参数
     * @return this
     */
    public P likeRight(String attributeName, Object value) {
        return likeRight(getAttribute(attributeName), value);
    }

    /**
     * likeRight
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P likeRight(SingleExpression<T, String> attribute, Object value) {
        return likeRight(attribute.execute(from, builder), value);
    }

    /**
     * likeRight
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P likeRight(Expression<String> attribute, Object value) {
        predicates.add(
                builder.like(attribute, "%" + value)
        );
        return (P) this;
    }

    //endregion

    //region likeLeft

    /**
     * likeLeft
     *
     * @param attributeName 字段
     * @param value         参数
     * @return this
     */
    public P likeLeft(String attributeName, Object value) {
        return likeLeft(getAttribute(attributeName), value);
    }

    /**
     * likeLeft
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P likeLeft(SingleExpression<T, String> attribute, Object value) {
        return likeLeft(attribute.execute(from, builder), value);
    }

    /**
     * likeLeft
     *
     * @param attribute 字段
     * @param value     参数
     * @return this
     */
    public P likeLeft(Expression<String> attribute, Object value) {
        predicates.add(
                builder.like(attribute, value + "%")
        );
        return (P) this;
    }


    //endregion likeLeft


    /**
     * and 连接
     *
     * @param predicate {{@link Predicate}}
     * @return this
     */
    public P and(Predicate predicate) {
        Predicate andPredicate = this.builder.and(build(), predicate);
        this.predicates = new ArrayList<>();
        this.predicates.add(andPredicate);
        return (P) this;
    }

    /**
     * and 连接
     *
     * @param that PredicateBuilder
     * @return this
     */
    public P and(PredicateBuilder<S, T, P> that) {
        Predicate andPredicate = this.builder.and(build(), that.build());
        this.predicates = new ArrayList<>();
        this.predicates.add(andPredicate);
        return (P) this;
    }

    /**
     * or 连接 Predicate
     *
     * @param predicate {@link Predicate}
     * @return this
     */
    public P or(Predicate predicate) {
        Predicate andPredicate = this.builder.or(build(), predicate);
        this.predicates = new ArrayList<>();
        this.predicates.add(andPredicate);
        return (P) this;
    }

    /**
     * or 连接
     *
     * @param that PredicateBuilder
     * @return this
     */
    public P or(PredicateBuilder<S, T, P> that) {
        Predicate andPredicate = this.builder.or(build(), that.build());
        this.predicates = new ArrayList<>();
        this.predicates.add(andPredicate);
        return (P) this;
    }

    /**
     * add 添加条件
     *
     * @param predicate Predicate
     * @return this
     */
    public P add(Predicate predicate) {
        this.predicates.add(predicate);
        return (P) this;
    }

//    /**
//     * @param condition
//     * @param filterBuilderConsumer
//     * @return
//     */
//    public P condition(boolean condition, Consumer<PredicateBuilder<S, T, P>> filterBuilderConsumer) {
//        if (condition) {
//            filterBuilderConsumer.accept(this);
//        }
//
//        return (P) this;
//
//    }
//
//    /**
//     * @param supplier
//     * @param filterBuilderConsumer
//     * @return
//     */
//    public P condition(BooleanSupplier supplier, Consumer<PredicateBuilder<S, T, P>> filterBuilderConsumer) {
//        if (supplier.getAsBoolean()) {
//            filterBuilderConsumer.accept(this);
//        }
//
//        return (P) this;
//
//    }

    public void clear() {
        predicates.clear();
    }

    public Predicate build() {
        return build(this.booleanOperator);
    }

    /**
     * @param booleanOperator {{@link Predicate.BooleanOperator}}
     * @return Predicate
     */
    public Predicate build(Predicate.BooleanOperator booleanOperator) {

        if (booleanOperator == Predicate.BooleanOperator.AND) {
            return builder.and(predicates.toArray((new Predicate[0])));
        } else {
            return builder.or(predicates.toArray((new Predicate[0])));
        }
    }
}
