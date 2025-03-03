package org.raven.hibernate.util;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.raven.commons.util.Lists;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ManagedTypeUtils {

    private final static Map<String, Map<String, String>> propertyColumnsMapAttributeMap = new ConcurrentHashMap<>();
    private final static Map<String, EntityPersister> entityPersisterMap = new ConcurrentHashMap<>();

    private ManagedTypeUtils() {
    }

    public static Set<String> attributeNames(ManagedType<?> managedType) {
        return managedType.getAttributes().stream()
                .map(Attribute::getName)
                .collect(Collectors.toSet());
    }

    public static List<Selection<?>> selections(From<?, ?> root) {
        return selections(root, false);
    }

    public static List<Selection<?>> selections(From<?, ?> root, boolean includeAssociationAttribute) {

        if (root.getModel() instanceof ManagedType<?>) {
            ManagedType<?> entityType = (ManagedType<?>) root.getModel();

            return entityType.getAttributes().stream()
                    .filter(e -> includeAssociationAttribute || !e.isAssociation())
                    .map(e -> {
                        return root.get(e.getName()).alias(e.getName());
                    })
                    .collect(Collectors.toList());

        }

        return Lists.newArrayList();
    }

    public static String getAttributeName(ManagedType<?> managedType, String propertyColumnName) {
        Map<String, String> columnsMap = columnsMapAttribute(managedType);
        return columnsMap.get(propertyColumnName);
    }

//    public static Map<String, String> columnsMapAttribute(Root<?> root) {
//        return columnsMapAttribute(root.getModel());
//    }

    public static Map<String, String> columnsMapAttribute(ManagedType<?> managedType) {

        EntityPersister entityPersister = getEntityPersister(managedType);

        return columnsMapAttribute(entityPersister, managedType.getJavaType());
    }

    public static Map<String, String> columnsMapAttribute(EntityPersister entityPersister, Class<?> entityClass) {

        Map<String, String> columnsMap = propertyColumnsMapAttributeMap.get(entityClass.getName());
        if (columnsMap != null) {
            return columnsMap;
        } else {
            columnsMap = new HashMap<>();
        }

        if (entityPersister instanceof Queryable) {
            Queryable queryable = (Queryable) entityPersister;
            for (String propertyName : queryable.getPropertyNames()) {
                String[] columns = queryable.toColumns(propertyName);
                for (String column : columns) {
                    columnsMap.put(column, propertyName);
                }
            }
        }

        propertyColumnsMapAttributeMap.putIfAbsent(entityClass.getName(), columnsMap);
        return columnsMap;
    }

    public static AttributeDefinition getAttributeDefinition(ManagedType<?> managedType, String attributeName) {

        EntityPersister entityPersister = getEntityPersister(managedType);
        if (entityPersister == null) {
            return null;
        }

        for (AttributeDefinition attribute : entityPersister.getAttributes()) {
            if (attribute.getName().equalsIgnoreCase(attributeName)) {
                return attribute;
            }
        }
        return null;
    }

    public static EntityPersister getEntityPersister(Metamodel metamodel, Class<?> entityClass) {

        if (metamodel instanceof MetamodelImplementor) {
            return ((MetamodelImplementor) metamodel).entityPersister(entityClass);
        }

        return null;
    }

    public static EntityPersister getEntityPersister(ManagedType<?> managedType) {

        Class<?> entityClass = managedType.getJavaType();
        EntityPersister entityPersister = entityPersisterMap.get(entityClass.getName());

        if (entityPersister == null) {

            SessionFactoryImplementor sessionFactoryImplementor = getSessionFactory(managedType);
            if (sessionFactoryImplementor != null) {
                MetamodelImplementor metamodel = sessionFactoryImplementor.getMetamodel();

                entityPersister = metamodel.entityPersister(entityClass);

                if (entityPersister != null) {
                    entityPersisterMap.putIfAbsent(entityClass.getName(), entityPersister);
                }
            }
        }

        return entityPersister;

    }

    private static SessionFactoryImplementor getSessionFactory(ManagedType<?> managedType) {

        if (managedType instanceof ManagedTypeDescriptor<?>) {
            return sessionFactory((ManagedTypeDescriptor<?>) managedType);
        }

        return null;
    }

    private static SessionFactoryImplementor sessionFactory(ManagedTypeDescriptor<?> managedTypeDescriptor) {
        return managedTypeDescriptor.makeSubGraph().sessionFactory();
    }
}
