package org.raven.hibernate.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * @author by yanfengf
 * date 2021/9/13 20:00
 */
public class FilterBuilder<T> extends PredicateBuilder<T, T, FilterBuilder<T>> {

    private final Root<T> root;

    public FilterBuilder(Root<T> root, CriteriaBuilder builder) {
        this(root, builder, Predicate.BooleanOperator.AND);
    }

    public FilterBuilder(Root<T> root, CriteriaBuilder builder, Predicate.BooleanOperator booleanOperator) {
        super(root, builder, booleanOperator);
        this.root = root;
    }


    public static <T> FilterBuilder<T> empty(Root<T> root, CriteriaBuilder builder) {
        return empty(root, builder, Predicate.BooleanOperator.AND);
    }

    public static <T> FilterBuilder<T> empty(Root<T> root, CriteriaBuilder builder, Predicate.BooleanOperator booleanOperator) {
        return new FilterBuilder<>(root, builder, booleanOperator);
    }

    public FilterBuilder<T> newFilterBuilder() {
        return new FilterBuilder<>(root, builder, Predicate.BooleanOperator.AND);
    }

    public FilterBuilder<T> newFilterBuilder(Predicate.BooleanOperator booleanOperator) {
        return new FilterBuilder<>(root, builder, booleanOperator);
    }

    /**
     * and 连接
     *
     * @param thatFilterExpression {@code f -> f.equal("name", "张三")}
     * @return this
     */
    public FilterBuilder<T> and(FilterExpression<T> thatFilterExpression) {
        Predicate andPredicate = this.builder.and(build(), thatFilterExpression.apply(this.newFilterBuilder()));
        super.clear();
        super.add(andPredicate);
        return this;
    }


    /**
     * or 连接
     *
     * @param thatFilterExpression {@code f -> f.equal("name", "张三")}
     * @return this
     */
    public FilterBuilder<T> or(FilterExpression<T> thatFilterExpression) {
        Predicate andPredicate = this.builder.or(build(), thatFilterExpression.apply(this.newFilterBuilder()));
        super.clear();
        super.add(andPredicate);
        return this;
    }

//    /**
//     * add 添加条件
//     *
//     * @param predicate
//     * @return
//     */
//    public FilterBuilder<T> add(Predicate predicate) {
//        super.add(predicate);
//        return this;
//    }

    public FilterBuilder<T> condition(boolean condition
            , Consumer<FilterBuilder<T>> filterBuilderConsumer) {

        if (condition) {
            filterBuilderConsumer.accept(this);
        }

        return this;

    }

    public FilterBuilder<T> condition(BooleanSupplier supplier
            , Consumer<FilterBuilder<T>> filterBuilderConsumer) {

        if (supplier.getAsBoolean()) {
            filterBuilderConsumer.accept(this);
        }

        return this;

    }

}
