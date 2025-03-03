package org.raven.hibernate.jpa;

import org.hibernate.persister.entity.EntityPersister;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.EntityManager;
import java.util.Map;

public interface EntityInformation<T, ID> extends EntityInterceptorInformation, JpaEntityInformation<T, ID> {

    Map<String, String> columnsMapAttribute();

    Class<T> getEntityType();

    boolean containsAttribute(String attributeName);

    EntityManager getEntityManager();

    EntityPersister getEntityPersister();

    String tenantIdAttributeName();

    String deleteAttributeName();
}
