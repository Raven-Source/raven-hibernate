package org.raven.hibernate.jpa;

import lombok.NonNull;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.raven.hibernate.function.CountDistinctFunction;
import org.raven.hibernate.util.ManagedTypeUtils;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author by yanfeng
 * date 2021/9/13 15:24
 */
public class SelectorBuilder<S, T> extends AbstractBuilder<S, T> {

    private final List<Selection<?>> selections = new ArrayList<>();

    public SelectorBuilder(From<S, T> from, CriteriaBuilder builder) {
        super(from, builder);
    }

    public static <S, T> SelectorBuilder<S, T> empty(From<S, T> from, CriteriaBuilder builder) {
        return new SelectorBuilder<>(from, builder);
    }

    public boolean isEmpty() {
        return selections.isEmpty();
    }

    public SelectorBuilder<S, T> select(@NonNull String... attributeNames) {

        for (String attributeName : attributeNames) {
            selections.add(getAttribute(attributeName));
        }

        return this;
    }

    public SelectorBuilder<S, T> select(@NonNull Map<String, String> attributes) {

        for (Map.Entry<String, String> e : attributes.entrySet()) {
            selections.add(getAttribute(e.getKey()).alias(e.getValue()));
        }

        return this;
    }

    public SelectorBuilder<S, T> select(@NonNull Expression<?>... attributes) {

        selections.addAll(Arrays.asList(attributes));

        return this;
    }

    public SelectorBuilder<S, T> select(@NonNull MultiExpression<T> attributes) {

        selections.addAll(attributes.execute(from, builder));

        return this;
    }

    public SelectorBuilder<S, T> allColumn() {
        return allColumn(false);
    }

    public SelectorBuilder<S, T> allColumn(boolean includeAssociationAttribute) {

        selections.addAll(ManagedTypeUtils.selections(from, includeAssociationAttribute));

        return this;
    }

    public SelectorBuilder<S, T> column(String attributeName) {

        return column(getAttribute(attributeName));
    }

    public SelectorBuilder<S, T> column(Expression<?> attribute) {

        return column(attribute, null);
    }

    public SelectorBuilder<S, T> column(String attributeName, String alias) {

        return column(getAttribute(attributeName), alias);
    }

    public SelectorBuilder<S, T> column(Expression<?> attribute, String alias) {

        selections.add(attribute.alias(alias));

        return this;
    }

    public <X> SelectorBuilder<S, T> withJoin(String joinAttributeName, SelectorExpression<X> joinSelector) {

        Join<T, X> join = getJoin(joinAttributeName);
        selections.addAll(joinSelector.apply(new SelectorBuilder<T, X>(join, builder)));

        return this;
    }

    public <R> SelectorBuilder<S, T> caseWhen(Consumer<CaseWhenBuilder<S, T, R>> caseWhenBuilder, String alias) {

        CriteriaBuilder.Case<R> selectCase = builder.selectCase();
        caseWhenBuilder.accept(new CaseWhenBuilder<>(selectCase, from, builder));
        selections.add(selectCase.alias(alias));
        return this;
    }

    public SelectorBuilder<S, T> max(String attributeName) {

        return max(getAttribute(attributeName));
    }

    public SelectorBuilder<S, T> max(String attributeName, String alias) {

        return max(getAttribute(attributeName), alias);
    }

    public <R extends Number> SelectorBuilder<S, T> max(SingleExpression<T, R> attribute) {

        return max(attribute.execute(from, builder));
    }

    public <R extends Number> SelectorBuilder<S, T> max(SingleExpression<T, R> attribute, String alias) {

        return max(attribute.execute(from, builder), alias);
    }

    public <R extends Number> SelectorBuilder<S, T> max(Expression<R> attribute) {

        return max(attribute, "max");
    }

    public <R extends Number> SelectorBuilder<S, T> max(Expression<R> attribute, String alias) {

        selections.add(builder.max(attribute).alias(alias));

        return this;
    }

    public SelectorBuilder<S, T> min(String attributeName) {

        return min(getAttribute(attributeName));
    }

    public SelectorBuilder<S, T> min(String attributeName, String alias) {

        return min(getAttribute(attributeName), alias);
    }

    public <R extends Number> SelectorBuilder<S, T> min(SingleExpression<T, R> attribute) {

        return min(attribute.execute(from, builder));
    }

    public <R extends Number> SelectorBuilder<S, T> min(SingleExpression<T, R> attribute, String alias) {

        return min(attribute.execute(from, builder), alias);
    }

    public <Y extends Number> SelectorBuilder<S, T> min(Expression<Y> attribute) {

        return min(attribute, "min");
    }

    public <Y extends Number> SelectorBuilder<S, T> min(Expression<Y> attribute, String alias) {

        selections.add(builder.min(attribute).alias(alias));

        return this;
    }

    public SelectorBuilder<S, T> count(String attributeName) {

        return count(getAttribute(attributeName));
    }

    public SelectorBuilder<S, T> count(String attributeName, String alias) {

        return count(getAttribute(attributeName), alias);
    }

    public <R> SelectorBuilder<S, T> count(SingleExpression<T, R> attribute) {

        return count(attribute.execute(from, builder));
    }

    public <R> SelectorBuilder<S, T> count(SingleExpression<T, R> attribute, String alias) {

        return count(attribute.execute(from, builder), alias);
    }

    public SelectorBuilder<S, T> count(Expression<?> attribute) {

        return count(attribute, "count");
    }

    public SelectorBuilder<S, T> count(Expression<?> attribute, String alias) {

        selections.add(builder.count(attribute).alias(alias));

        return this;
    }

    public SelectorBuilder<S, T> countDistinct(String attributeName) {

        return countDistinct(getAttribute(attributeName));
    }

    public SelectorBuilder<S, T> countDistinct(String attributeName, String alias) {

        return countDistinct(getAttribute(attributeName), alias);
    }

    public SelectorBuilder<S, T> countDistinct(Expression<?> attribute) {

        return countDistinct(attribute, "count");
    }

    public SelectorBuilder<S, T> countDistinct(Expression<?> attribute, String alias) {

        selections.add(builder.countDistinct(attribute).alias(alias));

        return this;
    }

    public SelectorBuilder<S, T> countDistinct(@NonNull Set<String> attributeNames, String alias) {

        return countDistinct(attributeNames.stream().map(this::getAttribute).collect(Collectors.toList()), alias);
    }

    public SelectorBuilder<S, T> countDistinct(@NonNull List<Expression<?>> attributes, String alias) {

        if (!attributes.isEmpty()) {
            if (attributes.size() == 1) {
                return countDistinct(attributes.iterator().next(), alias);

            } else {
                selections.add(
                        new CountDistinctFunction((CriteriaBuilderImpl) builder, attributes).alias(alias)
                );
            }
        }

        return this;
    }


    public SelectorBuilder<S, T> countDistinct(@NonNull MultiExpression<T> expression, String alias) {

        return countDistinct(expression.execute(from, builder), alias);

    }

    public SelectorBuilder<S, T> avg(String attributeName) {

        return avg(getAttribute(attributeName));
    }

    public SelectorBuilder<S, T> avg(String attributeName, String alias) {

        return avg(getAttribute(attributeName), alias);
    }

    public <Y extends Number> SelectorBuilder<S, T> avg(Expression<Y> attribute) {

        return avg(attribute, "avg");
    }

    public <Y extends Number> SelectorBuilder<S, T> avg(Expression<Y> attribute, String alias) {

        selections.add(builder.avg(attribute).alias(alias));

        return this;
    }

    public SelectorBuilder<S, T> sum(String attributeName) {

        return sum(getAttribute(attributeName));
    }

    public SelectorBuilder<S, T> sum(String attributeName, String alias) {

        return sum(getAttribute(attributeName), alias);
    }

    public <Y extends Number> SelectorBuilder<S, T> sum(Expression<Y> attribute) {

        return sum(attribute, "sum");
    }

    public <Y extends Number> SelectorBuilder<S, T> sum(Expression<Y> attribute, String alias) {

        selections.add(builder.sum(attribute).alias(alias));

        return this;
    }

    public <R> SelectorBuilder<S, T> exp(SingleExpression<T, R> expression) {

        return exp(expression, null);
    }

    public <R> SelectorBuilder<S, T> exp(SingleExpression<T, R> expression, String alias) {

        selections.add(expression.execute(from, builder).alias(alias));

        return this;
    }

    /**
     * @param condition condition
     * @param selectorBuilderConsumer SelectorBuilder
     * @return this SelectorBuilder
     */
    public SelectorBuilder<S, T> condition(boolean condition, Consumer<SelectorBuilder<S, T>> selectorBuilderConsumer) {
        if (condition) {
            selectorBuilderConsumer.accept(this);
        }

        return this;
    }

    /**
     * @param supplier condition
     * @param selectorBuilderConsumer SelectorBuilder
     * @return this SelectorBuilder
     */
    public SelectorBuilder<S, T> condition(BooleanSupplier supplier, Consumer<SelectorBuilder<S, T>> selectorBuilderConsumer) {
        if (supplier.getAsBoolean()) {
            selectorBuilderConsumer.accept(this);
        }

        return this;
    }

    public List<Selection<?>> build() {
        return selections;
    }
}
