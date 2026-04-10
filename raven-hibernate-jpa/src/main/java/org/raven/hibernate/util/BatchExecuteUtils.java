package org.raven.hibernate.util;

import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metamodel.mapping.AttributeMapping;
import org.hibernate.metamodel.mapping.EntityMappingType;
import org.hibernate.metamodel.mapping.ModelPart;
import org.hibernate.persister.entity.EntityPersister;
import org.raven.hibernate.jpa.JpaRepositorySupport;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Batch execution utility compatible with Hibernate 7.2.x
 */
public class BatchExecuteUtils {


    // JdbcValueBiConsumer 绑定器实现
    private static ModelPart.JdbcValueBiConsumer<PreparedStatement, Void> jdbcValueConsumer =
            (position, statement, unused, valuePart, selectableMapping) -> {
                try {
                    statement.setObject(position + 1, valuePart);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            };

    public static <T> void batchInsert(JpaRepositorySupport<T, ?> jpaRepository,
                                       List<T> entities,
                                       List<String> insertProperties,
                                       int batchSize,
                                       boolean includeId) {
        batchInsert(jpaRepository.entityInformation().getEntityPersister(), entities, batchSize, insertProperties, includeId);
    }

    public static <T> void batchInsert(JpaRepositorySupport<T, ?> jpaRepository,
                                       List<T> entities,
                                       int batchSize) {
        batchInsert(jpaRepository.entityInformation().getEntityPersister(), entities, batchSize, null, true);
    }

    public static <T> void batchInsert(EntityPersister persister,
                                       List<T> entities,
                                       int batchSize) {
        batchInsert(persister, entities, batchSize, null, true);
    }

    public static <T> void batchInsert(EntityPersister persister,
                                       List<T> entities,
                                       int batchSize,
                                       List<String> insertProperties,
                                       boolean includeId) {

        if (batchSize <= 0) throw new IllegalArgumentException("batchSize must be greater than 0");
        if (entities == null || entities.isEmpty()) return;

        Transaction tx = null;
        try (SessionImplementor session = persister.getFactory().openSession()) {
            tx = session.beginTransaction();

            EntityMappingType entityMapping = persister.getEntityMappingType();

            List<AttributeMapping> attrList = new ArrayList<>();
            List<String> columnList = new ArrayList<>();

            int attrCount = entityMapping.getNumberOfAttributeMappings();

            // includeId
            if (includeId) {
                columnList.addAll(List.of(persister.getIdentifierColumnNames()));
            }

            // iterate attributes
            for (int i = 0; i < attrCount; i++) {
                AttributeMapping attr = entityMapping.getAttributeMapping(i);
                String attrName = attr.getAttributeName();
                if (insertProperties == null || insertProperties.isEmpty() || insertProperties.contains(attrName)) {
                    attrList.add(attr);
                    columnList.addAll(List.of(persister.getPropertyColumnNames(attrName)));
                }
            }

            String placeholders = columnList.stream().map(c -> "?").collect(Collectors.joining(", "));
            String sql = "INSERT INTO " + persister.getRootTableName() + " (" + String.join(",", columnList) + ") VALUES (" + placeholders + ")";

            session.doWork(connection -> {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    int count = 0;


                    for (T entity : entities) {
                        int idx = 1;

                        // set id
                        if (includeId) {
                            Object idValue = persister.getIdentifier(entity, session);
                            persister.getIdentifierMapping().breakDownJdbcValues(idValue, 0, ps, null, jdbcValueConsumer, session);
                            idx += persister.getIdentifierColumnNames().length;
                        }

                        // set attributes
                        for (AttributeMapping attr : attrList) {
                            Object value = attr.getValue(entity);
                            idx += attr.breakDownJdbcValues(value, idx - 1, ps, null, jdbcValueConsumer, session);
                        }

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
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    // ---------------- batchUpdate ----------------

    public static <T> void batchUpdate(JpaRepositorySupport<T, ?> jpaRepository,
                                       List<T> entities,
                                       int batchSize) {
        batchUpdate(jpaRepository.entityInformation().getEntityPersister(), entities, batchSize, null);
    }

    public static <T> void batchUpdate(JpaRepositorySupport<T, ?> jpaRepository,
                                       List<T> entities,
                                       int batchSize,
                                       List<String> updateProperties) {
        batchUpdate(jpaRepository.entityInformation().getEntityPersister(), entities, batchSize, updateProperties);
    }

    public static <T> void batchUpdate(EntityPersister persister,
                                       List<T> entities,
                                       int batchSize) {
        batchUpdate(persister, entities, batchSize, null);
    }

    public static <T> void batchUpdate(EntityPersister persister,
                                       List<T> entities,
                                       int batchSize,
                                       List<String> updateProperties) {

        if (batchSize <= 0) throw new IllegalArgumentException("batchSize must be greater than 0");
        if (entities == null || entities.isEmpty()) return;

        Transaction tx = null;
        try (SessionImplementor session = persister.getFactory().openSession()) {
            tx = session.beginTransaction();

            EntityMappingType entityMapping = persister.getEntityMappingType();
            List<AttributeMapping> attrList = new ArrayList<>();
            List<String> columnList = new ArrayList<>();

            int attrCount = entityMapping.getNumberOfAttributeMappings();

            for (int i = 0; i < attrCount; i++) {
                AttributeMapping attr = entityMapping.getAttributeMapping(i);
                String attrName = attr.getAttributeName();
                if (updateProperties == null || updateProperties.isEmpty() || updateProperties.contains(attrName)) {
                    attrList.add(attr);
                    columnList.addAll(List.of(persister.getPropertyColumnNames(attrName)));
                }
            }

            String setClause = columnList.stream().map(c -> c + "=?").collect(Collectors.joining(", "));
            String whereClause = String.join(" AND ", List.of(persister.getIdentifierColumnNames()).stream().map(c -> c + "=?").toList());
            String sql = "UPDATE " + persister.getRootTableName() + " SET " + setClause + " WHERE " + whereClause;

            session.doWork(connection -> {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    int count = 0;

                    for (T entity : entities) {
                        int idx = 1;

                        // set attributes
                        for (AttributeMapping attr : attrList) {
                            Object value = attr.getValue(entity);
                            idx += attr.breakDownJdbcValues(value, idx - 1, ps, null, jdbcValueConsumer, session);
                        }

                        // set id
                        Object idValue = persister.getIdentifier(entity, session);
                        idx += persister.getIdentifierMapping().breakDownJdbcValues(idValue, idx - 1, ps, null, jdbcValueConsumer, session);

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
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}