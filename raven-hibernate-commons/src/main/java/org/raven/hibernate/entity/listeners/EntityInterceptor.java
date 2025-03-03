package org.raven.hibernate.entity.listeners;

import org.springframework.data.repository.core.EntityInformation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;

/**
 * date 2022/7/26 10:58
 */
public interface EntityInterceptor {

    default void prePersist(Object entity) {

    }

    default void preUpdate(Object entity) {

    }

    default void preUpdate(CriteriaUpdate<?> criteriaUpdate, CriteriaBuilder criteriaBuilder, EntityInformation<?, ?> entityInformation) {
    }
}
