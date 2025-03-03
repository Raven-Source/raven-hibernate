package org.raven.hibernate.util;//package org.raven.hibernate.utils;
//
//import org.raven.commons.constant.CommonConstant;
//import org.raven.commons.context.impl.IgnoreContext;
//import org.raven.spring.commons.utils.ContextHolderProvider;
//import org.raven.commons.context.Context;
//import org.raven.commons.context.ContextHolder;
//
//public class TenantUtils {
//
//    private TenantUtils() {
//    }
//
//    public static Long getTenantId() {
//        ContextHolder contextHolder = ContextHolderProvider.getContextHolder();
//        Context context = contextHolder.getContext();
//        if (isIgnoreContext(context)) {
//            return null;
//        } else {
//            Object tenantId = contextHolder.getContext().getAttribute(CommonConstant.TENANT_ID);
//            if (tenantId == null) {
//                throw new RuntimeException("not find tenantId from context");
//            } else if (tenantId.getClass() == Long.class) {
//                return (long) tenantId;
//            } else {
//                return tenantId instanceof Number ? ((Number) tenantId).longValue() : Long.parseLong(tenantId.toString());
//            }
//        }
//    }
//
//    private static boolean isIgnoreContext(Context context) {
//        return IgnoreContext.class.isAssignableFrom(context.getClass());
//    }
//}
