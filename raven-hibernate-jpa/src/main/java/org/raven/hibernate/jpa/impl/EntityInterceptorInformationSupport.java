package org.raven.hibernate.jpa.impl;//package org.raven.hibernate.jpa.impl;
//
//
//import org.raven.hibernate.entity.listeners.EntityInterceptor;
//import org.raven.hibernate.jpa.EntityInterceptorInformation;
//import org.raven.hibernate.utils.EntityInterceptorUtils;
//
//import java.util.List;
//
///**
// * @author by yanfeng
// * date 2021/9/26 21:39
// */
//public class EntityInterceptorInformationSupport<TEntity> implements EntityInterceptorInformation {
//
//    private final List<EntityInterceptor> interceptors;
//
//    public EntityInterceptorInformationSupport(Class<TEntity> entityClass) {
//
//        this.interceptors = EntityInterceptorUtils.getInterceptors(entityClass);
//    }
//
//    @Override
//    public List<EntityInterceptor> getInterceptors() {
//        return interceptors;
//    }
//}
