package org.raven.hibernate.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Selection;
import java.util.List;

/**
 * @author yanfeng
 * date 2021.07.20 22:04
 */
@FunctionalInterface
public interface Selector<T> {

    List<Selection<?>> select(From<?, T> from, CriteriaBuilder builder);
}
