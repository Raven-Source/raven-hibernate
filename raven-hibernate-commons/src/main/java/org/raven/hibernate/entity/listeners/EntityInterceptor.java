package org.raven.hibernate.entity.listeners;

import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.repository.core.EntityInformation;


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
