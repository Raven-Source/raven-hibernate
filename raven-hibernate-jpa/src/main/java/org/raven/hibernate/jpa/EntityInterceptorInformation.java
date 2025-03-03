package org.raven.hibernate.jpa;


import org.raven.hibernate.entity.listeners.EntityInterceptor;

import java.util.List;

/**
 * date 2021/9/26 21:39
 */
public interface EntityInterceptorInformation {
    List<EntityInterceptor> getInterceptors();
}
