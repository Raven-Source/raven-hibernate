package org.raven.hibernate.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.util.function.Function;

public class CaseWhenBuilder<S, T, R> extends AbstractBuilder<S, T> {

    private final CriteriaBuilder.Case<R> selectCase;

    public CaseWhenBuilder(CriteriaBuilder.Case<R> selectCase, From<S, T> from, CriteriaBuilder builder) {
        super(from, builder);
        this.selectCase = selectCase;
    }

    public static <S, T, R> CaseWhenBuilder<S, T, R> empty(CriteriaBuilder.Case<R> selectCase, From<S, T> from, CriteriaBuilder builder) {
        return new CaseWhenBuilder<>(selectCase, from, builder);
    }

    public CaseWhenBuilder<S, T, R> when(final Function<CaseWhenPredicateBuilder<S, T>, Predicate> predicate, R result) {
        selectCase.when(predicate.apply(CaseWhenPredicateBuilder.empty(from, builder)), result);
        return this;
    }

    public CaseWhenBuilder<S, T, R> when(final Function<CaseWhenPredicateBuilder<S, T>, Predicate> predicate, Expression<? extends R> result) {
        selectCase.when(predicate.apply(CaseWhenPredicateBuilder.empty(from, builder)), result);
        return this;
    }

    public CaseWhenBuilder<S, T, R> otherwise(R result) {
        selectCase.otherwise(result);
        return this;
    }

    public CaseWhenBuilder<S, T, R> otherwise(Expression<? extends R> result) {
        selectCase.otherwise(result);
        return this;
    }


}
