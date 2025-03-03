package org.raven.hibernate.entity.id;//package org.raven.hibernate.entity.id;
//
//import org.hibernate.HibernateException;
//import org.hibernate.engine.spi.SharedSessionContractImplementor;
//import org.hibernate.id.IdentifierGenerator;
//import org.raven.spring.commons.util.SpringContextUtils;
//
//import java.io.Serializable;
//
//public class DefaultIdentifierGenerator implements IdentifierGenerator {
//
//    private final IdGenerator idGenerator;
//
//    public DefaultIdentifierGenerator() {
//        this.idGenerator = SpringContextUtils.getBean(IdGenerator.class);
//    }
//
//    @Override
//    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
//        return idGenerator.nextId();
//    }
//}
