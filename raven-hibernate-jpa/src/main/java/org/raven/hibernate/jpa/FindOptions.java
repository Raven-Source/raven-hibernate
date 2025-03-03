package org.raven.hibernate.jpa;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Sort;

import java.util.List;

@Setter
@Getter
@Accessors(fluent = true)
public class FindOptions<T> {

    private Filter<T> filter;

    private Selector<T> select;

    private GroupBy<T> groupBy;

    private Filter<T> having;

    private Sorter<T> sort;

    private Joiner<T> join;

    private int skip;

    private int limit;

    public FindOptions<T> filter(final Filter<T> filter) {
        this.filter = filter;
        return this;
    }

    public FindOptions<T> filter(final FilterExpression<T> filterExpression) {
        filter = filterExpression != null ? filterExpression.toFilter() : null;
        return this;
    }

    public FindOptions<T> select(final Selector<T> selector) {
        this.select = selector;
        return this;
    }

    public FindOptions<T> select(final SelectorExpression<T> selectorExpression) {
        select = selectorExpression != null ? selectorExpression.toSelector() : null;
        return this;
    }

    public FindOptions<T> groupBy(final GroupBy<T> groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public FindOptions<T> groupBy(final GroupByExpression<T> groupByExpression) {
        groupBy = groupByExpression != null ? groupByExpression.toGroupBy() : null;
        return this;
    }

    public FindOptions<T> having(final Filter<T> having) {
        this.having = having;
        return this;
    }

    public FindOptions<T> having(final FilterExpression<T> havingExpression) {
        having = havingExpression != null ? havingExpression.toFilter() : null;
        return this;
    }

    public FindOptions<T> sort(final Sorter<T> sort) {
        this.sort = sort;
        return this;
    }

    public FindOptions<T> sort(final List<Sort.Order> orders) {
        this.sort = Sorter.toSorter(orders);
        return this;
    }

    public FindOptions<T> sort(final SorterExpression<T> sortExpression) {
        sort = sortExpression != null ? sortExpression.toSorter() : null;
        return this;
    }

    public FindOptions<T> join(final Joiner<T> joiner) {
        this.join = joiner;
        return this;
    }

    public FindOptions<T> join(final JoinExpression<T> joinExpression) {
        this.join = joinExpression != null ? joinExpression.toJoin() : null;
        return this;
    }

    private FindOptions() {
    }

    public static <T> FindOptions<T> empty() {
        return new FindOptions<>();
    }

}
