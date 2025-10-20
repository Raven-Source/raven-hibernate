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

public class BatchExecuteUtils {


    public static <T> void batchInsert(JpaRepositorySupport<T, ?> jpaRepository,
                                       List<T> entities,
                                       int batchSize,
                                       List<String> insertProperties,
                                       boolean includeId) {
        batchInsert(jpaRepository.entityInformation().getEntityPersister(), entities, batchSize, insertProperties, includeId);
    }

    public static <T> void batchInsert(EntityPersister entityPersister,
                                       List<T> entities,
                                       int batchSize) {
        batchInsert(entityPersister, entities, batchSize, null, true);
    }

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

        try (SessionImplementor session = persister.getFactory().openSession()) {
            Transaction tx = session.beginTransaction();


            String tableName = persister.getRootTableName();
            String[] idColumns = persister.getIdentifierColumnNames();
            Type idType = persister.getIdentifierType();

            // 构建列和类型列表（固定顺序）
            List<String> columnList = new ArrayList<>();
            List<Type> typeList = new ArrayList<>();
            List<Integer> propIndexList = new ArrayList<>();

            // 主键
            if (includeId) {
                columnList.addAll(List.of(idColumns));
                typeList.add(idType);
            }

            // 普通属性
            if (insertProperties == null || insertProperties.isEmpty()) {
                // 插入全部字段
                String[] allPropNames = persister.getPropertyNames();
                for (int i = 0; i < allPropNames.length; i++) {
                    propIndexList.add(i);
                    columnList.addAll(List.of(persister.getPropertyColumnNames(i)));
                    typeList.add(persister.toType(allPropNames[i]));
                }
            } else {
                // 插入指定字段
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

                        // 主键
                        if (includeId) {
                            Object idValue = persister.getIdentifier(entity, session);
                            idType.nullSafeSet(ps, idValue, idx++, session);
                        }

                        // 普通属性
                        for (int i = 0; i < propIndexList.size(); i++) {
                            int propIndex = propIndexList.get(i);
                            Object value = persister.getPropertyValue(entity, propIndex);
                            // typeList 的第一个元素可能是主键，所以要对齐
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

                    // 提交剩余
                    if (count % batchSize != 0) {
                        ps.executeBatch();
                        connection.commit();
                    }
                }
            });

            tx.commit();
        }

    }

    public static <T> void batchUpdate(JpaRepositorySupport<T, ?> jpaRepository,
                                       List<T> entities,
                                       int batchSize,
                                       List<String> updateProperties) {
        batchUpdate(jpaRepository.entityInformation().getEntityPersister(), entities, batchSize, updateProperties);
    }


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

        try (SessionImplementor session = persister.getFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            String tableName = persister.getRootTableName();
            String[] idColumns = persister.getIdentifierColumnNames();
            Type idType = persister.getIdentifierType();

            // 构建 update 列
            List<String> columnList = new ArrayList<>();
            List<Type> typeList = new ArrayList<>();
            List<Integer> propIndexList = new ArrayList<>();

            String[] allPropNames = persister.getPropertyNames();

            if (updateProperties == null || updateProperties.isEmpty()) {
                // 更新全部非主键字段
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

            // 构建 SQL
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

                        // 普通属性
                        for (int i = 0; i < propIndexList.size(); i++) {
                            int propIndex = propIndexList.get(i);
                            Object value = persister.getPropertyValue(entity, propIndex);
                            Type type = typeList.get(i);
                            type.nullSafeSet(ps, value, idx, session);
                            idx += persister.getPropertyColumnSpan(propIndex);
                        }

                        // 主键放最后
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
        }
    }


}
