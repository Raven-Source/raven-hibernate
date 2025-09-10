package org.raven.hibernate.jpa;

import lombok.Getter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class JoinOnPredicateBuilder<T, Y> extends PredicateBuilder<T, Y, JoinOnPredicateBuilder<T, Y>> {

    @Getter
    private final From<T, T> root;

    public JoinOnPredicateBuilder(Join<T, Y> from, From<T, T> root, CriteriaBuilder builder) {
        super(from, builder);
        this.root = root;
    }

    public static <T, Y> JoinOnPredicateBuilder<T, Y> empty(Join<T, Y> from, From<T, T> root, CriteriaBuilder builder) {
        return new JoinOnPredicateBuilder<T, Y>(from, root, builder);
    }

    /**
     * @param condition condition
     * @param filterBuilderConsumer JoinOnPredicateBuilder
     * @return this JoinOnPredicateBuilder
     */
    public JoinOnPredicateBuilder<T, Y> condition(boolean condition
            , Consumer<JoinOnPredicateBuilder<T, Y>> filterBuilderConsumer) {

        if (condition) {
            filterBuilderConsumer.accept(this);
        }

        return this;

    }

    /**
     * @param supplier condition
     * @param filterBuilderConsumer JoinOnPredicateBuilder
     * @return this JoinOnPredicateBuilder
     */
    public JoinOnPredicateBuilder<T, Y> condition(BooleanSupplier supplier
            , Consumer<JoinOnPredicateBuilder<T, Y>> filterBuilderConsumer) {

        if (supplier.getAsBoolean()) {
            filterBuilderConsumer.accept(this);
        }

        return this;

    }

}
