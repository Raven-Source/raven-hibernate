package org.raven.hibernate.jpa;

//import org.raven.hibernate.criteria.CriteriaQueryRoot;

import javax.annotation.Nullable;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class JoinBuilder<T> extends AbstractBuilder<T, T> {

    private final List<Join<T, ?>> joins = new ArrayList<>();
//    private final CriteriaQuery<?> criteriaQuery;

    public JoinBuilder(From<T, T> root, CriteriaBuilder builder) {
        super(root, builder);
//        this.criteriaQuery = root.criteriaQuery();
    }

    public static <T> JoinBuilder<T> empty(From<T, T> root, CriteriaBuilder builder) {
        return new JoinBuilder<>(root, builder);
    }

    /**
     * join
     *
     * @param attributeName 字段
     * @return this
     */
    public JoinBuilder<T> join(String attributeName) {

        return join(attributeName, JoinType.INNER);
    }

    /**
     * join
     *
     * @param attributeName 字段
     * @return this
     */
    public JoinBuilder<T> leftJoin(String attributeName) {

        return join(attributeName, JoinType.LEFT);
    }

    /**
     * join
     *
     * @param attributeName 字段
     * @return this
     */
    public JoinBuilder<T> rightJoin(String attributeName) {

        return join(attributeName, JoinType.RIGHT);
    }

    /**
     * join
     *
     * @param attributeName 字段
     * @param joinType      {@link JoinType}
     * @return this
     */
    public JoinBuilder<T> join(String attributeName, JoinType joinType) {

        return join(attributeName, joinType, (Consumer<Join<T, Object>>) null);
    }

    /**
     * join
     *
     * @param attributeName 字段
     * @param joinConsumer  Join
     * @param <Y>           the target type of the join
     * @return this
     */
    public <Y> JoinBuilder<T> leftJoin(String attributeName, @Nullable final Consumer<Join<T, Y>> joinConsumer) {

        return join(attributeName, JoinType.LEFT, joinConsumer);
    }

    /**
     * join
     *
     * @param attributeName   字段
     * @param joinOnPredicate JoinOnPredicateBuilder
     * @param <Y>             the target type of the join
     * @return this
     */
    public <Y> JoinBuilder<T> leftJoin(String attributeName
            , @Nullable final Function<JoinOnPredicateBuilder<T, Y>, Predicate> joinOnPredicate) {

        return join(attributeName, JoinType.LEFT, joinOnPredicate);
    }

    /**
     * join
     *
     * @param attributeName 字段
     * @param joinConsumer  Join
     * @param <Y>           the target type of the join
     * @return this
     */
    public <Y> JoinBuilder<T> rightJoin(String attributeName, @Nullable final Consumer<Join<T, Y>> joinConsumer) {

        return join(attributeName, JoinType.RIGHT, joinConsumer);
    }

    /**
     * join
     *
     * @param attributeName   字段
     * @param joinOnPredicate JoinOnPredicateBuilder
     * @param <Y>             the target type of the join
     * @return this
     */
    public <Y> JoinBuilder<T> rightJoin(String attributeName
            , @Nullable final Function<JoinOnPredicateBuilder<T, Y>, Predicate> joinOnPredicate) {

        return join(attributeName, JoinType.RIGHT, joinOnPredicate);
    }

    /**
     * join
     *
     * @param attributeName 字段
     * @param joinConsumer  Join
     * @param joinType      {@link JoinType}
     * @param <Y>           the target type of the join
     * @return this
     */
    public <Y> JoinBuilder<T> join(String attributeName, JoinType joinType, @Nullable final Consumer<Join<T, Y>> joinConsumer) {
        Join<T, Y> join = from.join(attributeName, joinType);
        if (joinConsumer != null) {
            joinConsumer.accept(join);
        }
        joins.add(join);
        return this;
    }

    /**
     * join
     *
     * @param attributeName   字段
     * @param joinOnPredicate JoinOnPredicateBuilder
     * @param <Y>             the target type of the JoinOnPredicateBuilder
     * @return this
     */
    public <Y> JoinBuilder<T> join(String attributeName
            , @Nullable final Function<JoinOnPredicateBuilder<T, Y>, Predicate> joinOnPredicate) {
        return join(attributeName, JoinType.INNER, joinOnPredicate);
    }

    /**
     * join
     *
     * @param attributeName   字段
     * @param joinType        {@link JoinType}
     * @param joinOnPredicate JoinOnPredicateBuilder
     * @param <Y>             the target type of the JoinOnPredicateBuilder
     * @return this
     */
    public <Y> JoinBuilder<T> join(String attributeName, JoinType joinType
            , @Nullable final Function<JoinOnPredicateBuilder<T, Y>, Predicate> joinOnPredicate) {
        Join<T, Y> join = from.join(attributeName, joinType);
        if (joinOnPredicate != null) {
            Predicate onPredicate = joinOnPredicate.apply(JoinOnPredicateBuilder.empty(join, from, builder));
            if (onPredicate != null) {
                join.on(onPredicate);
            }
        }

        joins.add(join);
        return this;
    }

    /**
     * join fetch
     *
     * @param attributeName 字段
     * @return this
     */
    public JoinBuilder<T> fetch(String attributeName) {
        return fetch(attributeName, JoinType.INNER);
    }

    /**
     * join fetch
     *
     * @param attributeName 字段
     * @param joinType      {@link JoinType}
     * @param <Y>           the target type of the fetch
     * @return this
     */
    public <Y> JoinBuilder<T> fetch(String attributeName, JoinType joinType) {
        return fetch(attributeName, joinType, (Consumer<Fetch<T, Y>>) null);
    }

    /**
     * join fetch
     *
     * @param attributeName 字段
     * @param joinType      {@link JoinType}
     * @param joinConsumer  Join
     * @param <Y>           the target type of the fetch
     * @return this
     */
    public <Y> JoinBuilder<T> fetch(String attributeName, JoinType joinType, @Nullable final Consumer<Fetch<T, Y>> joinConsumer) {
        Fetch<T, Y> fetch = from.fetch(attributeName, joinType);
        if (joinConsumer != null) {
            joinConsumer.accept(fetch);
        }
        return this;
    }

    public List<Join<T, ?>> build() {
        return joins;
    }

}
