package org.raven.hibernate.jpa;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(fluent = true)
public class CountOptions<T> {

    private Filter<T> filter;

    private GroupBy<T> groupBy;

    private int skip;

    public CountOptions<T> filter(final Filter<T> filter) {
        this.filter = filter;
        return this;
    }

    public CountOptions<T> groupBy(final GroupBy<T> groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public CountOptions<T> filter(final FilterExpression<T> filterExpression) {
        filter = filterExpression != null ? filterExpression.toFilter() : null;
        return this;
    }

    public CountOptions<T> groupBy(final GroupByExpression<T> groupByExpression) {
        groupBy = groupByExpression != null ? groupByExpression.toGroupBy() : null;
        return this;
    }

    private CountOptions() {
    }

    public static <T> CountOptions<T> empty() {
        return new CountOptions<>();
    }

}
