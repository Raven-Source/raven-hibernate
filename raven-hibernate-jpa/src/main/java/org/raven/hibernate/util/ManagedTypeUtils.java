package org.raven.hibernate.util;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.metamodel.model.domain.internal.EntityTypeImpl;
import org.hibernate.metamodel.model.domain.spi.JpaMetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;

import java.lang.reflect.Field;
import java.util.*;
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

        return new ArrayList<>();
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

        if (entityPersister instanceof AbstractEntityPersister) {
            AbstractEntityPersister abstractEntityPersister = (AbstractEntityPersister) entityPersister;
            for (String propertyName : entityPersister.getPropertyNames()) {
                String[] columns = abstractEntityPersister.toColumns(propertyName);
                for (String column : columns) {
                    columnsMap.put(column, propertyName);
                }
            }
        }


        propertyColumnsMapAttributeMap.putIfAbsent(entityClass.getName(), columnsMap);
        return columnsMap;
    }

    public static Attribute<?, ?> getAttribute(Root<?> root, String attributeName) {

        return getAttribute(root.getModel(), attributeName);
    }

    public static Attribute<?, ?> getAttribute(EntityType<?> entityType, String attributeName) {

        for (Attribute<?, ?> attribute : entityType.getAttributes()) {

            if (attribute.getName().equalsIgnoreCase(attributeName)) {
                return attribute;
            }
        }

        return null;
    }

    public static EntityPersister getEntityPersister(Metamodel metamodel, Class<?> entityClass) {

        if (metamodel instanceof JpaMetamodelImplementor) {
            return ((JpaMetamodelImplementor) metamodel).getMappingMetamodel().getEntityDescriptor(entityClass);
        } else if (metamodel instanceof MappingMetamodel) {
            return ((MappingMetamodel) metamodel).getEntityDescriptor(entityClass);
        }

        return null;
    }

    public static EntityPersister getEntityPersister(ManagedType<?> managedType) {

        Class<?> entityClass = managedType.getJavaType();
        EntityPersister entityPersister = entityPersisterMap.get(entityClass.getName());

        if (entityPersister == null) {

            JpaMetamodelImplementor metamodel = getMetamodel(managedType);
            entityPersister = getEntityPersister(metamodel, entityClass);
//            if (sessionFactoryImplementor != null) {
//                Metamodel metamodel = sessionFactoryImplementor.getMetamodel();
//
//                entityPersister = getEntityPersister(metamodel, entityClass);
//
//                if (entityPersister != null) {
//                    entityPersisterMap.putIfAbsent(entityClass.getName(), entityPersister);
//                }
//            }
        }

        return entityPersister;

    }

    private static JpaMetamodelImplementor getMetamodel(ManagedType<?> managedType) {
        if (managedType instanceof EntityTypeImpl<?> entityTypeImpl) {
            try {
                Field field = EntityTypeImpl.class.getDeclaredField("metamodel");
                field.setAccessible(true);
                return (JpaMetamodelImplementor) field.get(entityTypeImpl);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return null;
    }

//    private static SessionFactoryImplementor getSessionFactory(ManagedType<?> managedType) {
//
////        if (managedType instanceof ManagedTypeDescriptor<?>) {
////            return sessionFactory((ManagedTypeDescriptor<?>) managedType);
////        }
//
//        return null;
//    }

//    private static SessionFactoryImplementor sessionFactory(ManagedTypeDescriptor<?> managedTypeDescriptor) {
//        return managedTypeDescriptor.makeSubGraph().sessionFactory();
//    }
}
