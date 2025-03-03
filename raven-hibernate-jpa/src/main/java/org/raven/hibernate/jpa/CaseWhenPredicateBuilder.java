package org.raven.hibernate.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CaseWhenPredicateBuilder<T, Y> extends PredicateBuilder<T, Y, CaseWhenPredicateBuilder<T, Y>> {

    public CaseWhenPredicateBuilder(From<T, Y> from, CriteriaBuilder builder) {
        super(from, builder);
    }

    public static <T, Y> CaseWhenPredicateBuilder<T, Y> empty(From<T, Y> from, CriteriaBuilder builder) {
        return new CaseWhenPredicateBuilder<>(from, builder);
    }

    public CaseWhenPredicateBuilder<T, Y> condition(boolean condition
            , Consumer<CaseWhenPredicateBuilder<T, Y>> filterBuilderConsumer) {

        if (condition) {
            filterBuilderConsumer.accept(this);
        }

        return this;

    }

    public CaseWhenPredicateBuilder<T, Y> condition(BooleanSupplier supplier
            , Consumer<CaseWhenPredicateBuilder<T, Y>> filterBuilderConsumer) {

        if (supplier.getAsBoolean()) {
            filterBuilderConsumer.accept(this);
        }

        return this;

    }
}
