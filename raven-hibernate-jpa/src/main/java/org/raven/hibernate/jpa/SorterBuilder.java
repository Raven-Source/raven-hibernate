package org.raven.hibernate.jpa;

import lombok.NonNull;
import org.springframework.data.domain.Sort;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class SorterBuilder<S, T> extends AbstractBuilder<S, T> {

    private final List<Order> orders = new ArrayList<>();

    public SorterBuilder(From<S, T> from, CriteriaBuilder builder) {
        super(from, builder);
    }

    public static <S, T> SorterBuilder<S, T> empty(From<S, T> from, CriteriaBuilder builder) {
        return new SorterBuilder<S, T>(from, builder);
    }

    public SorterBuilder<S, T> asc(String attributeName) {

        return asc(getAttribute(attributeName));
    }

    public SorterBuilder<S, T> asc(SingleExpression<T, ?> attribute) {

        return asc(attribute.execute(from, builder));
    }

    public SorterBuilder<S, T> asc(Expression<?> attribute) {

        orders.add(builder.asc(attribute));

        return this;
    }

    public SorterBuilder<S, T> desc(String attributeName) {

        return desc(getAttribute(attributeName));
    }

    public SorterBuilder<S, T> desc(SingleExpression<T, ?> attribute) {

        return desc(attribute.execute(from, builder));
    }

    public SorterBuilder<S, T> desc(Expression<?> attribute) {

        orders.add(builder.desc(attribute));

        return this;
    }

    public SorterBuilder<S, T> by(Sort sort) {
        return by(sort.toList());
    }

    public SorterBuilder<S, T> by(List<Sort.Order> sortOrders) {
        for (Sort.Order order : sortOrders) {
            Expression<?> expression = getAttribute(order.getProperty());
            switch (order.getDirection()) {
                case ASC:
                    orders.add(builder.asc(expression));
                    break;
                case DESC:
                    orders.add(builder.desc(expression));
                    break;
            }
        }
        return this;
    }

    public <X> SorterBuilder<S, T> withJoin(String joinAttributeName, SorterExpression<X> joinSorter) {

        Join<T, X> join = getJoin(joinAttributeName);
        orders.addAll(joinSorter.apply(new SorterBuilder<T, X>(join, builder)));

        return this;
    }

    public SorterBuilder<S, T> condition(boolean condition, @NonNull Consumer<SorterBuilder<S, T>> sorterBuilderConsumer) {
        if (condition) {
            sorterBuilderConsumer.accept(this);
        }
        return this;
    }

    public SorterBuilder<S, T> condition(BooleanSupplier supplier, @NonNull Consumer<SorterBuilder<S, T>> sorterBuilderConsumer) {
        if (supplier.getAsBoolean()) {
            sorterBuilderConsumer.accept(this);
        }
        return this;
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public List<Order> build() {
        return orders;
    }
}
