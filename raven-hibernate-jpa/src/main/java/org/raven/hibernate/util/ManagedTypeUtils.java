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
import org.hibernate.metamodel.mapping.AttributeMapping;
import org.hibernate.metamodel.mapping.EntityMappingType;
import org.hibernate.metamodel.model.domain.JpaMetamodel;
import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.hibernate.metamodel.model.domain.spi.JpaMetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ManagedTypeUtils {

    private static final Map<String, Map<String, String>> propertyColumnsMapAttributeMap = new ConcurrentHashMap<>();
    private static final Map<String, EntityPersister> entityPersisterMap = new ConcurrentHashMap<>();

    private ManagedTypeUtils() {
    }

    // ================= 属性名 =================

    public static Set<String> attributeNames(ManagedType<?> managedType) {
        return managedType.getAttributes().stream()
                .map(Attribute::getName)
                .collect(Collectors.toSet());
    }

    // ================= select =================

    public static List<Selection<?>> selections(From<?, ?> root) {
        return selections(root, false);
    }

    public static List<Selection<?>> selections(From<?, ?> root, boolean includeAssociationAttribute) {

        if (root.getModel() instanceof ManagedType<?>) {
            ManagedType<?> entityType = (ManagedType<?>) root.getModel();

            return entityType.getAttributes().stream()
                    .filter(e -> includeAssociationAttribute || !e.isAssociation())
                    .map(e -> root.get(e.getName()).alias(e.getName()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    // ================= column -> property =================

    public static String getAttributeName(ManagedType<?> managedType, String columnName) {
        return columnsMapAttribute(managedType).get(columnName);
    }

    public static Map<String, String> columnsMapAttribute(ManagedType<?> managedType) {
        EntityPersister persister = getEntityPersister(managedType);
        return columnsMapAttribute(persister, managedType.getJavaType());
    }

    /**
     * Hibernate 7 正确实现
     */
    public static Map<String, String> columnsMapAttribute(EntityPersister persister, Class<?> entityClass) {

        Map<String, String> cache = propertyColumnsMapAttributeMap.get(entityClass.getName());
        if (cache != null) return cache;

        Map<String, String> result = new HashMap<>();

        EntityMappingType mapping = persister.getEntityMappingType();

        int count = mapping.getNumberOfAttributeMappings();

        for (int i = 0; i < count; i++) {
            AttributeMapping attr = mapping.getAttributeMapping(i);
            String attrName = attr.getAttributeName();

            // ⭐ Hibernate 7 正确方式：遍历列
            attr.forEachSelectable((index, selectable) -> {
                String column = selectable.getSelectionExpression();
                result.put(column, attrName);
            });
        }

        propertyColumnsMapAttributeMap.putIfAbsent(entityClass.getName(), result);
        return result;
    }

    // ================= Attribute =================

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

    // ================= EntityPersister =================

    public static EntityPersister getEntityPersister(Metamodel metamodel, Class<?> entityClass) {

        if (metamodel instanceof JpaMetamodelImplementor jpa) {
            return jpa.getMappingMetamodel().getEntityDescriptor(entityClass);
        }

        if (metamodel instanceof MappingMetamodel mapping) {
            return mapping.getEntityDescriptor(entityClass);
        }

        throw new RuntimeException("Unsupported Metamodel: " + metamodel.getClass());
    }

    public static EntityPersister getEntityPersister(ManagedType<?> managedType) {

        Class<?> entityClass = managedType.getJavaType();

        return entityPersisterMap.computeIfAbsent(entityClass.getName(), key -> {

            Metamodel metamodel = getMetamodel(managedType);

            EntityPersister persister = getEntityPersister(metamodel, entityClass);

            if (persister == null) {
                throw new RuntimeException("EntityPersister not found for " + entityClass.getName());
            }

            return persister;
        });
    }

    private static JpaMetamodel getMetamodel(ManagedType<?> managedType) {

        if (managedType instanceof JpaMetamodel) {
            // 极少数情况
            return (JpaMetamodel) managedType;
        }

        if (managedType instanceof ManagedDomainType<?> entityType) {
            return entityType.getMetamodel();
        }

        throw new RuntimeException("Cannot extract JpaMetamodelImplementor from ManagedType: "
                + managedType.getClass());
    }

    // ================= SessionFactory =================

    public static SessionFactoryImplementor sessionFactory(EntityPersister entityPersister) {
        return entityPersister.getFactory();
    }
}