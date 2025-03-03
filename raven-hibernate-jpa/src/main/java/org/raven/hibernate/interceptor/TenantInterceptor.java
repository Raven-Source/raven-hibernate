package org.raven.hibernate.interceptor;//package org.raven.hibernate.interceptor;
//
//import org.raven.commons.contract.Tenant;
//import org.raven.hibernate.entity.listeners.EntityInterceptor;
//import org.raven.hibernate.utils.TenantUtils;
//
//public class TenantInterceptor implements EntityInterceptor {
//
//    public TenantInterceptor() {
//    }
//
//    @Override
//    public void prePersist(Object entity) {
//        if (entity instanceof Tenant) {
//            Long tenantId = TenantUtils.getTenantId();
//            if (tenantId != null) {
//                ((Tenant) entity).setTenantId(tenantId);
//            }
//        }
//    }
//}
