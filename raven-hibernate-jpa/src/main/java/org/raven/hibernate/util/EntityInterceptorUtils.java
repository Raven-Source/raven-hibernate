package org.raven.hibernate.util;

import lombok.extern.slf4j.Slf4j;
import org.raven.hibernate.entity.listeners.EntityInterceptor;

import javax.persistence.EntityListeners;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yi.liang
 * date 2021/9/22 19:41
 */
@Slf4j
public class EntityInterceptorUtils {

    private final static Map<Class<?>, EntityInterceptor> entityInterceptorCached = new ConcurrentHashMap<>();

    private EntityInterceptorUtils() {
    }

    public static List<EntityInterceptor> getInterceptors(Class<?> entityClass) {

        Set<EntityInterceptor> entityInterceptors = new HashSet<>();

        List<EntityListeners> entityListeners = AnnotationUtils.findAllInheritanceAnnotation(entityClass, EntityListeners.class);

        for (EntityListeners entityListener : entityListeners) {

            Class<?>[] classes = entityListener.value();

            for (Class<?> clazz : classes) {

                if (!EntityInterceptor.class.isAssignableFrom(clazz))
                    continue;

                EntityInterceptor entityInterceptor = entityInterceptorCached.get(clazz);

                if (entityInterceptor == null) {
                    try {
                        entityInterceptor = (EntityInterceptor) clazz.getDeclaredConstructor().newInstance();
                        entityInterceptorCached.putIfAbsent(clazz, entityInterceptor);
                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }

                entityInterceptors.add(entityInterceptor);
            }
        }

        return new ArrayList<>(entityInterceptors);
    }

}
