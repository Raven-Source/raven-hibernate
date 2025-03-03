package org.raven.hibernate.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.function.Function;

/**
 * @author yanfeng
 * date 2020.06.21 16:07
 */
@FunctionalInterface
public interface Filter<T> {

    default boolean ignoreTenant() {
        return false;
    }

    default boolean ignoreDeletable() {
        return false;
    }

    Expression<Boolean> toPredicate(Root<T> root, CriteriaBuilder builder);

    static <R> Filter<R> of(Function<FilterBuilder<R>, Expression<Boolean>> filterBuilder
            , boolean ignoreTenant) {

        return of(filterBuilder, ignoreTenant, false);
    }

    static <R> Filter<R> of(Function<FilterBuilder<R>, Expression<Boolean>> filterBuilder
            , boolean ignoreTenant, boolean ignoreDeletable) {

        Filter<R> filter = (root, builder) -> filterBuilder.apply(new FilterBuilder<>(root, builder));
        return of(filter, ignoreTenant, ignoreDeletable);
    }

    static <R> Filter<R> of(Filter<R> that, boolean ignoreTenant) {

        return of(that, ignoreTenant, false);
    }

    static <R> Filter<R> of(Filter<R> that, boolean ignoreTenant, boolean ignoreDeletable) {

        FilterImpl<R> r = new FilterImpl<>();
        r.ignoreTenant = ignoreTenant;
        r.ignoreDeletable = ignoreDeletable;
        r.that = that;

        return r;
    }

    static class FilterImpl<T> implements Filter<T> {

        private Filter<T> that;

        private boolean ignoreTenant = false;
        private boolean ignoreDeletable = false;

        @Override
        public boolean ignoreTenant() {
            return ignoreTenant;
        }

        @Override
        public boolean ignoreDeletable() {
            return ignoreDeletable;
        }

        @Override
        public Expression<Boolean> toPredicate(Root<T> root, CriteriaBuilder builder) {
            return that.toPredicate(root, builder);
        }

        private FilterImpl() {
        }

    }

//    Predicate toPredicate(FilterBuilder<T> filterBuilder);
}
