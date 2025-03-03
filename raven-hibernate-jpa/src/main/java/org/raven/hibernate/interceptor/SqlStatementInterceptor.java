package org.raven.hibernate.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.raven.hibernate.sql.SqlFunctionConditionCleaner;

@Slf4j
public class SqlStatementInterceptor extends EmptyInterceptor {

    private final SqlFunctionConditionCleaner conditionCleaner;

    public SqlStatementInterceptor() {

        conditionCleaner = new SqlFunctionConditionCleaner();

        log.info("hibernate interceptor load with SqlStatementInterceptor");
    }

    @Override
    public String onPrepareStatement(String sql) {
        return conditionCleaner.optimize(sql);
    }

//    @Override
//    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
//        return super.onLoad(entity, id, state, propertyNames, types);
//    }

    //    @Override
//    public void onLoad(String entityName, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
//        super.onLoad(entityName, id, state, propertyNames, types);
//        // 在查询时，自动应用过滤器
//        Session session = HibernateUtils.getCurrentSession();
//        org.hibernate.Filter filter = session.enableFilter(TenantFilter.TENANT_FILTER_NAME);
//        filter.setParameter("tenantId", TenantUtils.getCurrentTenantId());
//    }

//    @Override
//    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
//        super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
//        // 在保存更新时，自动填充租户ID字段
//        if (entity instanceof TenantEntity) {
//            for (String propertyName : propertyNames) {
//                if ("tenant_id".equals(propertyName)) {
////                    currentState[i] = TenantUtils.getCurrentTenantId();
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void preFlush(Iterator entities) {
//
//        while (entities.hasNext()) {
//            Object x = entities.next();
//            System.out.println(x.getClass());
//        }
//
//    }
//
//    @Override
//    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
//
//        if (entity instanceof Tenant) {
//            ((Tenant) entity).setTenantId(1000L);
//
//            int index = getIndex(propertyNames, TenantEntity.Fields.tenantId);
//            if (index != -1) {
//                state[index] = 1000L;
//            }
//
//        }
//
//        return super.onSave(entity, id, state, propertyNames, types);
//    }
//
//    @Override
//    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
//        super.onDelete(entity, id, state, propertyNames, types);
//    }

//
//    private int getIndex(String[] propertyNames, String columnName) {
//        for (int i = 0; i < propertyNames.length; i++) {
//            if (columnName.equals(propertyNames[i])) {
//                return i;
//            }
//        }
//        return -1;
//    }
}