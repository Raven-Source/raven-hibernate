package org.raven.hibernate.jpa.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.persister.entity.EntityPersister;
import org.raven.commons.data.Deletable;
import org.raven.hibernate.entity.listeners.EntityInterceptor;
import org.raven.hibernate.jpa.EntityInformation;
import org.raven.hibernate.util.EntityInterceptorUtils;
import org.raven.hibernate.util.ManagedTypeUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class DefaultEntityInformation<T, ID> implements EntityInformation<T, ID> {

    private final EntityManager entityManager;
    private final List<EntityInterceptor> interceptors;
    private final JpaEntityInformation<T, ID> entityInformation;

    private final EntityPersister persister;
    private final Set<String> attributeNames;
    private final Map<String, String> columnsMapAttribute;

    public DefaultEntityInformation(EntityManager entityManager, JpaEntityInformation<T, ID> entityInformation) {
        this.entityInformation = entityInformation;

        this.entityManager = entityManager;
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType<T> entityType = metamodel.entity(getEntityType());

        Class<T> entityClass = entityInformation.getJavaType();
        this.interceptors = EntityInterceptorUtils.getInterceptors(entityClass);

        this.persister = ManagedTypeUtils.getEntityPersister(metamodel, entityClass);
        this.attributeNames = ManagedTypeUtils.attributeNames(entityType);
        this.columnsMapAttribute = ManagedTypeUtils.columnsMapAttribute(persister, entityClass);
    }

//    public <S extends T> void insert(Iterable<S> entities) {
////        entityPersister.insert()
//        SharedSessionContractImplementor session = entityManager
//                .unwrap(SharedSessionContractImplementor.class);
//
//        for (S entity : entities) {
//
//            Serializable id = persister.getIdentifierGenerator().generate(session, entity);
//            Object[] state = persister.getPropertyValues(entity);
//            if (persister.isVersioned()) {
//                boolean substitute = Versioning.seedVersion(
//                        state,
//                        persister.getVersionProperty(),
//                        persister.getVersionType(),
//                        session
//                );
//                if (substitute) {
//                    persister.setPropertyValues(entity, state);
//                }
//            }
//            if (id == IdentifierGeneratorHelper.POST_INSERT_INDICATOR) {
//                id = persister.insert(state, entity, session);
//            } else {
//                persister.insert(id, state, entity, session);
//            }
//            persister.setIdentifier(entity, id, session);
//        }
//
//
//    }


    @Override
    public List<EntityInterceptor> getInterceptors() {
        return interceptors;
    }

    @Override
    public Map<String, String> columnsMapAttribute() {
        return Collections.emptyMap();
    }

    @Override
    public boolean containsAttribute(String attributeName) {
        return attributeNames.contains(attributeName) || columnsMapAttribute.containsKey(attributeName);
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public EntityPersister getEntityPersister() {
        return persister;
    }

    @Override
    public String tenantIdAttributeName() {
        return "tenantId";
    }

    @Override
    public String deleteAttributeName() {
        return Deletable.DELETED;
    }

    public Class<T> getEntityType() {
        return entityInformation.getJavaType();
    }

    @Nullable
    @Override
    public SingularAttribute<? super T, ?> getIdAttribute() {
        return entityInformation.getIdAttribute();
    }

    @Override
    public boolean hasCompositeId() {
        return entityInformation.hasCompositeId();
    }

    @Override
    public Iterable<String> getIdAttributeNames() {
        return entityInformation.getIdAttributeNames();
    }

    @Nullable
    @Override
    public Object getCompositeIdAttributeValue(Object id, String idAttribute) {
        return entityInformation.getCompositeIdAttributeValue(id, idAttribute);
    }

    @Override
    public String getEntityName() {
        return entityInformation.getEntityName();
    }

    @Override
    public boolean isNew(T t) {
        return entityInformation.isNew(t);
    }

    @Nullable
    @Override
    public ID getId(T t) {
        return entityInformation.getId(t);
    }

    @Override
    public Class<ID> getIdType() {
        return entityInformation.getIdType();
    }

    @Override
    public Class<T> getJavaType() {
        return entityInformation.getJavaType();
    }

}
