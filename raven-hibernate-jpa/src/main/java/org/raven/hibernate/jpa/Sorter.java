package org.raven.hibernate.jpa;

import org.springframework.data.domain.Sort;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Order;
import java.util.List;

@FunctionalInterface
public interface Sorter<T> {
    List<Order> sort(From<?, T> from, CriteriaBuilder builder);

    static <T> Sorter<T> toSorter(Sort sort) {
        return toSorter(sort.toList());
    }

    static <T> Sorter<T> toSorter(List<Sort.Order> orders) {
        return (from, builder) -> new SorterBuilder<>(from, builder).by(orders).build();
    }
}
