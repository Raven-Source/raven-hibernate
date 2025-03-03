package org.raven.hibernate.persister;

import org.hibernate.HibernateException;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;

public class CustomSingleTableEntityPersister extends SingleTableEntityPersister {

    public CustomSingleTableEntityPersister(PersistentClass persistentClass, EntityDataAccess cacheAccessStrategy, NaturalIdDataAccess naturalIdRegionAccessStrategy, PersisterCreationContext creationContext) throws HibernateException {
        super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext);
    }

//    @Override
//    protected Insert createInsert() {
//        return super.createInsert();
//    }

//    public void insertBatch(Object[] fields, Object object, SharedSessionContractImplementor session)
//            throws HibernateException {
//        // apply any pre-insert in-memory value generation
//        preInsertInMemoryValueGeneration( fields, object, session );
//
//        final int span = getTableSpan();
//        final Serializable id;
//        if ( getEntityMetamodel().isDynamicInsert() ) {
//            // For the case of dynamic-insert="true", we need to generate the INSERT SQL
//            boolean[] notNull = getPropertiesToInsert( fields );
//            id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
//            for ( int j = 1; j < span; j++ ) {
//                insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
//            }
//        }
//        else {
//            // For the case of dynamic-insert="false", use the static SQL
//            id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
//            for ( int j = 1; j < span; j++ ) {
//                insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
//            }
//        }
////        return id;
//    }
}
