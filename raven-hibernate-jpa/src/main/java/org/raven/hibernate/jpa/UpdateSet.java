package org.raven.hibernate.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

/**
 * @author yanfeng
 * date 2020.06.21 15:59
 */
@FunctionalInterface
public interface UpdateSet<T> {

    void toSet(CriteriaUpdate<T> update, Root<T> root, CriteriaBuilder builder);

}
