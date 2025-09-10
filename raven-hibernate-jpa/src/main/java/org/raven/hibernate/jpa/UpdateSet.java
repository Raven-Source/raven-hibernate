package org.raven.hibernate.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

/**
 * @author yanfeng
 * date 2020.06.21 15:59
 */
@FunctionalInterface
public interface UpdateSet<T> {

    void toSet(CriteriaUpdate<T> update, Root<T> root, CriteriaBuilder builder);

}
