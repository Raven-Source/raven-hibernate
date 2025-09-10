package org.raven.hibernate.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Selection;
import java.util.List;

/**
 * @author yanfeng
 * date 2021.07.20 22:04
 */
@FunctionalInterface
public interface Selector<T> {

    List<Selection<?>> select(From<?, T> from, CriteriaBuilder builder);
}
