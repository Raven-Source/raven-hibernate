package org.raven.hibernate.util;

import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityPersisterUtils {

    public static <T> void bigBatchInsert(EntityPersister entityPersister,
                                          List<T> entities,
                                          int batchSize) {
        bigBatchInsert(entityPersister, entities, batchSize, null);
    }

    public static <T> void bigBatchInsert(EntityPersister entityPersister,
                                          List<T> entities,
                                          int batchSize,
                                          List<String> insertProperties) {

        if (!(entityPersister instanceof AbstractEntityPersister persister)) {
            throw new IllegalArgumentException("entityPersister must be AbstractEntityPersister");
        }

        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }

        if (entities == null || entities.isEmpty()) {
            return;
        }

//        try (StatelessSession session = persister.getFactory().openStatelessSession()) {
//            Transaction tx = session.beginTransaction();
//
//            for (int i = 0; i < entities.size(); i++) {
//
//                session.insert(entities.get(i));
//
//                // 到达批次大小，提交事务
//                if ((i + 1) % batchSize == 0) {
//                    tx.commit();                 // 提交
//                    tx = session.beginTransaction(); // 开启新事务
//                }
//            }
//
//            // 提交剩余未提交的数据
//            if (entities.size() % batchSize != 0) {
//                tx.commit();
//            }
//        }

        try (SessionImplementor session = persister.getFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.doWork(connection -> {
                String tableName = persister.getRootTableName();
                String[] idColumns = persister.getIdentifierColumnNames();
//                String idProperty = persister.getIdentifierPropertyName();
                Type idType = persister.getIdentifierType();

                // 构建列和类型列表（固定顺序）
                List<String> columnList = new ArrayList<>();
                List<Type> typeList = new ArrayList<>();
                List<Integer> propIndexList = new ArrayList<>();

                // 主键
                columnList.addAll(List.of(idColumns));
                typeList.add(idType);

                // 普通属性
                if (insertProperties == null || insertProperties.isEmpty()) {
                    // 插入全部字段
                    String[] allPropNames = persister.getPropertyNames();
                    for (int i = 0; i < allPropNames.length; i++) {
                        propIndexList.add(i);
                        columnList.addAll(List.of(persister.getPropertyColumnNames(i)));
                        String allPropName = allPropNames[i];
                        typeList.add(persister.toType(allPropName));
                    }
                } else {
                    // 插入指定字段
                    for (String propName : insertProperties) {
                        int propIndex = -1;
                        String[] allPropNames = persister.getPropertyNames();
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

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    int count = 0;

                    for (T entity : entities) {
                        int idx = 1;

                        // 主键
                        Object idValue = persister.getIdentifier(entity, session);
                        idType.nullSafeSet(ps, idValue, idx++, session);

                        // 普通属性
                        for (int i = 0; i < propIndexList.size(); i++) {
                            int propIndex = propIndexList.get(i);
                            Object value = persister.getPropertyValue(entity, propIndex);
                            Type type = typeList.get(i + 1); // +1 因为第一个是主键
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
}
