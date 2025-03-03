package org.raven.hibernate.util;//package org.raven.hibernate.utils;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.persistence.EntityManager;
//import javax.persistence.metamodel.EntityType;
//import javax.persistence.metamodel.Metamodel;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class EntityManagerResolver {
//
//    private static final List<EntityManager> entityManagers = new ArrayList<>();
//    private static final Map<Class<?>, EntityManager> entityManagerCache = new ConcurrentHashMap<>();
//
//    private EntityManagerResolver() {
//    }
//
//    @Autowired
//    private void setEntityManagers(final Map<String, EntityManager> entityManagers) {
//        EntityManagerResolver.entityManagers.addAll(entityManagers.values());
//    }
//
//    /**
//     * 根据实体类的 Class 类型获取对应的 EntityManager
//     *
//     * @param entityClass 实体类
//     * @return 对应的 EntityManager
//     */
//    public static EntityManager getEntityManager(Class<?> entityClass) {
//
//        // 先检查缓存
//        Optional<EntityManager> entityManager = Optional.ofNullable(entityManagerCache.get(entityClass));
//        if (entityManager.isPresent()) {
//            return entityManager.get();
//        }
//
//        // 遍历所有 EntityManager，寻找匹配的实体
//        entityManager = entityManagers.stream()
//                .filter(em -> containsEntity(em, entityClass))
//                .findFirst();
//
//        // 如果找到，缓存并返回
//        entityManager.ifPresent(em -> entityManagerCache.putIfAbsent(entityClass, em));
//        return entityManager.orElseThrow(() -> new IllegalArgumentException("No EntityManager found for class: " + entityClass));
//    }
//
//
//    /**
//     * 检查 EntityManager 是否包含指定的实体类
//     *
//     * @param entityManager EntityManager
//     * @param entityClass   实体类
//     * @return 是否包含
//     */
//    private static boolean containsEntity(EntityManager entityManager, Class<?> entityClass) {
//        Metamodel metamodel = entityManager.getMetamodel();
//        return metamodel.getEntities().stream()
//                .map(EntityType::getJavaType)
//                .anyMatch(type -> type.equals(entityClass));
//    }
//}
