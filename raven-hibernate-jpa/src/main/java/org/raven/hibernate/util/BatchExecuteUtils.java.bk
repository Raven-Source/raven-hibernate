package org.raven.hibernate.util;

import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.raven.hibernate.jpa.JpaRepositorySupport;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for batch execution operations on Hibernate entities.
 * Provides methods for batch insert and update operations to improve performance
 * when dealing with large amounts of data.
 */
public class BatchExecuteUtils {

    /**
     * Performs batch insert operation on entities with specified properties
     * 
     * @param jpaRepository the JPA repository support instance
     * @param entities the list of entities to insert
     * @param insertProperties the list of property names to insert, null or empty means all properties
     * @param batchSize the batch size for insertion
     * @param includeId whether to include the ID field in the insert operation
     * @param <T> the entity type
     */
    public static <T> void batchInsert(JpaRepositorySupport<T, ?> jpaRepository,
                                       List<T> entities,
                                       List<String> insertProperties,
                                       int batchSize,
                                       boolean includeId) {
        batchInsert(jpaRepository.entityInformation().getEntityPersister(), entities, batchSize, insertProperties, includeId);
    }

    /**
     * Performs batch insert operation on entities with default settings (include ID, all properties)
     * 
     * @param jpaRepository the JPA repository support instance
     * @param entities the list of entities to insert
     * @param batchSize the batch size for insertion
     * @param <T> the entity type
     */
    public static <T> void batchInsert(JpaRepositorySupport<T, ?> jpaRepository,
                                       List<T> entities,
                                       int batchSize) {
        batchInsert(jpaRepository.entityInformation().getEntityPersister(), entities, batchSize, null, true);
    }

    /**
     * Performs batch insert operation on entities with default settings (include ID, all properties)
     * 
     * @param entityPersister the entity persister
     * @param entities the list of entities to insert
     * @param batchSize the batch size for insertion
     * @param <T> the entity type
     */
    public static <T> void batchInsert(EntityPersister entityPersister,
                                       List<T> entities,
                                       int batchSize) {
        batchInsert(entityPersister, entities, batchSize, null, true);
    }

    /**
     * Performs batch insert operation on entities with specified properties
     * 
     * @param entityPersister the entity persister
     * @param entities the list of entities to insert
     * @param batchSize the batch size for insertion
     * @param insertProperties the list of property names to insert, null or empty means all properties
     * @param includeId whether to include the ID field in the insert operation
     * @param <T> the entity type
     */
    public static <T> void batchInsert(EntityPersister entityPersister,
                                       List<T> entities,
                                       int batchSize,
                                       List<String> insertProperties,
                                       boolean includeId) {

        if (!(entityPersister instanceof AbstractEntityPersister persister)) {
            throw new IllegalArgumentException("entityPersister must be AbstractEntityPersister");
        }

        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }

        if (entities == null || entities.isEmpty()) {
            return;
        }

        Transaction tx = null;
        try (SessionImplementor session = persister.getFactory().openSession()) {
            tx = session.beginTransaction();


            String tableName = persister.getRootTableName();
            String[] idColumns = persister.getIdentifierColumnNames();
            Type idType = persister.getIdentifierType();

            // Build column and type lists (fixed order)
            List<String> columnList = new ArrayList<>();
            List<Type> typeList = new ArrayList<>();
            List<Integer> propIndexList = new ArrayList<>();

            // Primary key
            if (includeId) {
                columnList.addAll(List.of(idColumns));
                typeList.add(idType);
            }

            // Regular properties
            if (insertProperties == null || insertProperties.isEmpty()) {
                // Insert all fields
                String[] allPropNames = persister.getPropertyNames();
                for (int i = 0; i < allPropNames.length; i++) {
                    propIndexList.add(i);
                    columnList.addAll(List.of(persister.getPropertyColumnNames(i)));
                    typeList.add(persister.toType(allPropNames[i]));
                }
            } else {
                // Insert specified fields
                String[] allPropNames = persister.getPropertyNames();
                for (String propName : insertProperties) {
                    int propIndex = -1;
                    for (int i = 0; i < allPropNames.length; i++) {
                        if (allPropNames[i].equals(propName)) {
                            propIndex = i;
                            break;
                        }
                    }
                    if (propIndex < 0) {
                        throw new IllegalArgumentException("Property not found: " + propName);
                    }

                    propIndexList.add(propIndex);
                    String[] colNames = persister.getPropertyColumnNames(propIndex);
                    columnList.addAll(List.of(colNames));
                    typeList.add(persister.toType(propName));
                }
            }

            String placeholders = columnList.stream().map(c -> "?").collect(Collectors.joining(", "));
            String sql = "INSERT INTO " + tableName + " (" + String.join(",", columnList) + ") VALUES (" + placeholders + ")";


            session.doWork(connection -> {

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    int count = 0;

                    for (T entity : entities) {
                        int idx = 1;

                        // Primary key
                        if (includeId) {
                            Object idValue = persister.getIdentifier(entity, session);
                            idType.nullSafeSet(ps, idValue, idx++, session);
                        }

                        // Regular properties
                        for (int i = 0; i < propIndexList.size(); i++) {
                            int propIndex = propIndexList.get(i);
                            Object value = persister.getPropertyValue(entity, propIndex);
                            // The first element of typeList may be the primary key, so alignment is needed
                            Type type = includeId ? typeList.get(i + 1) : typeList.get(i);
                            type.nullSafeSet(ps, value, idx, session);
                            idx += persister.getPropertyColumnSpan(propIndex);
                        }

                        ps.addBatch();
                        count++;

                        if (count % batchSize == 0) {
                            ps.executeBatch();
                            connection.commit();
                        }
                    }

                    // Commit remaining
                    if (count % batchSize != 0) {
                        ps.executeBatch();
                        connection.commit();
                    }
                }
            });

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }

    }

    /**
     * Performs batch update operation on entities with default settings (all properties)
     * 
     * @param jpaRepository the JPA repository support instance
     * @param entities the list of entities to update
     * @param batchSize the batch size for update
     * @param <T> the entity type
     */
    public static <T> void batchUpdate(JpaRepositorySupport<T, ?> jpaRepository,
                                       List<T> entities,
                                       int batchSize) {
        batchUpdate(jpaRepository.entityInformation().getEntityPersister(), entities, batchSize, null);
    }

    /**
     * Performs batch update operation on entities with specified properties
     * 
     * @param jpaRepository the JPA repository support instance
     * @param entities the list of entities to update
     * @param batchSize the batch size for update
     * @param updateProperties the list of property names to update, null or empty means all properties
     * @param <T> the entity type
     */
    public static <T> void batchUpdate(JpaRepositorySupport<T, ?> jpaRepository,
                                       List<T> entities,
                                       int batchSize,
                                       List<String> updateProperties) {
        batchUpdate(jpaRepository.entityInformation().getEntityPersister(), entities, batchSize, updateProperties);
    }

    /**
     * Performs batch update operation on entities with default settings (all properties)
     * 
     * @param entityPersister the entity persister
     * @param entities the list of entities to update
     * @param batchSize the batch size for update
     * @param <T> the entity type
     */
    public static <T> void batchUpdate(EntityPersister entityPersister,
                                       List<T> entities,
                                       int batchSize) {
        batchUpdate(entityPersister, entities, batchSize, null);
    }

    /**
     * Performs batch update operation on entities with specified properties
     * 
     * @param entityPersister the entity persister
     * @param entities the list of entities to update
     * @param batchSize the batch size for update
     * @param updateProperties the list of property names to update, null or empty means all properties
     * @param <T> the entity type
     */
    public static <T> void batchUpdate(EntityPersister entityPersister,
                                       List<T> entities,
                                       int batchSize,
                                       List<String> updateProperties) {

        if (!(entityPersister instanceof AbstractEntityPersister persister)) {
            throw new IllegalArgumentException("entityPersister must be AbstractEntityPersister");
        }

        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }

        if (entities == null || entities.isEmpty()) {
            return;
        }

        Transaction tx = null;
        try (SessionImplementor session = persister.getFactory().openSession()) {
            tx = session.beginTransaction();

            String tableName = persister.getRootTableName();
            String[] idColumns = persister.getIdentifierColumnNames();
            Type idType = persister.getIdentifierType();

            // Build update columns
            List<String> columnList = new ArrayList<>();
            List<Type> typeList = new ArrayList<>();
            List<Integer> propIndexList = new ArrayList<>();

            String[] allPropNames = persister.getPropertyNames();

            if (updateProperties == null || updateProperties.isEmpty()) {
                // Update all non-primary key fields
                for (int i = 0; i < allPropNames.length; i++) {
                    propIndexList.add(i);
                    columnList.addAll(List.of(persister.getPropertyColumnNames(i)));
                    typeList.add(persister.toType(allPropNames[i]));
                }
            } else {
                for (String propName : updateProperties) {
                    int propIndex = -1;
                    for (int i = 0; i < allPropNames.length; i++) {
                        if (allPropNames[i].equals(propName)) {
                            propIndex = i;
                            break;
                        }
                    }
                    if (propIndex < 0) {
                        throw new IllegalArgumentException("Property not found: " + propName);
                    }

                    propIndexList.add(propIndex);
                    columnList.addAll(List.of(persister.getPropertyColumnNames(propIndex)));
                    typeList.add(persister.toType(propName));
                }
            }

            // Build SQL
            String setClause = columnList.stream()
                    .map(c -> c + "=?")
                    .collect(Collectors.joining(", "));

            String whereClause = String.join(" AND ",
                    List.of(idColumns).stream().map(c -> c + "=?").toList());

            String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;

            session.doWork(connection -> {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    int count = 0;

                    for (T entity : entities) {
                        int idx = 1;

                        // Regular properties
                        for (int i = 0; i < propIndexList.size(); i++) {
                            int propIndex = propIndexList.get(i);
                            Object value = persister.getPropertyValue(entity, propIndex);
                            Type type = typeList.get(i);
                            type.nullSafeSet(ps, value, idx, session);
                            idx += persister.getPropertyColumnSpan(propIndex);
                        }

                        // Put primary key last
                        Object idValue = persister.getIdentifier(entity, session);
                        idType.nullSafeSet(ps, idValue, idx, session);

                        ps.addBatch();
                        count++;

                        if (count % batchSize == 0) {
                            ps.executeBatch();
                            connection.commit();
                        }
                    }

                    if (count % batchSize != 0) {
                        ps.executeBatch();
                        connection.commit();
                    }
                }
            });

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

}