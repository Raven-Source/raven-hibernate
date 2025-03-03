package org.raven.hibernate.jpa;

import lombok.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class GroupByBuilder<S, T> extends AbstractBuilder<S, T> {

    private final List<Expression<?>> groupings = new ArrayList<>();

    public GroupByBuilder(From<S, T> from, CriteriaBuilder builder) {
        super(from, builder);
    }

    public static <S, T> GroupByBuilder<S, T> empty(From<S, T> from, CriteriaBuilder builder) {
        return new GroupByBuilder<>(from, builder);
    }

//    public GroupByBuilder(Class<T> entityType, CriteriaBuilder builder) {
//        super(entityType, builder);
//    }

    public boolean isEmpty() {
        return groupings.isEmpty();
    }

    public GroupByBuilder<S, T> groupBy(@NonNull String... attributeNames) {

        for (String attributeName : attributeNames) {
            groupings.add(getAttribute(attributeName));
        }

        return this;
    }

    public GroupByBuilder<S, T> groupBy(@NonNull Expression<?>... expressions) {

        groupings.addAll(Arrays.asList(expressions));

        return this;

    }

    public GroupByBuilder<S, T> groupBy(@NonNull MultiExpression<T> expression) {

        groupings.addAll(expression.execute(from, builder));

        return this;

    }

    public <X> GroupByBuilder<S, T> withJoin(String joinAttributeName, GroupByExpression<X> joinGroupBy) {

        Join<T, X> join = getJoin(joinAttributeName);
        groupings.addAll(joinGroupBy.apply(new GroupByBuilder<T, X>(join, builder)));

        return this;
    }

    public GroupByBuilder<S, T> condition(boolean condition, @NonNull Consumer<GroupByBuilder<S, T>> groupByBuilderConsumer) {
        if (condition) {
            groupByBuilderConsumer.accept(this);
        }

        return this;

    }

    public GroupByBuilder<S, T> condition(BooleanSupplier supplier, @NonNull Consumer<GroupByBuilder<S, T>> groupByBuilderConsumer) {

        if (supplier.getAsBoolean()) {
            groupByBuilderConsumer.accept(this);
        }

        return this;

    }

    public List<Expression<?>> build() {
        return groupings;
    }

}
