package org.raven.hibernate.convert;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.descriptor.java.BasicJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.java.spi.JsonJavaType;
import org.hibernate.type.descriptor.jdbc.JsonJdbcType;
import org.hibernate.type.spi.TypeConfigurationAware;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class JsonType
        extends BasicUserType<Object>
        implements DynamicParameterizedType, TypeConfigurationAware, UserType<Object> {

    //    private final ObjectMapper objectMapper;
    protected BasicJavaType<Object> javaType;

    public JsonType() {
//        objectMapper = SpringContextUtils.getBean(ObjectMapper.class);
    }

    @Override
    public void setParameterValues(Properties parameters) {
        super.setParameterValues(parameters);

//        javaType = (BasicJavaType) typeConfiguration.getJavaTypeRegistry().getDescriptor(javaTypeClass);
        javaType = new JsonJavaType<>(javaTypeClass, ImmutableMutabilityPlan.INSTANCE, typeConfiguration);
        jdbcType = JsonJdbcType.INSTANCE;
    }

    @Override
    public Class<Object> returnedClass() {
        return Object.class;
    }

    @Override
    protected Class<?> javaTypeForJdbcType(Class<Object> javaTypeClass) {
        return String.class;
    }

    @Override
    public boolean equals(Object x, Object y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) {
        return x.hashCode();
    }

    @Override
    public Serializable disassemble(Object value) {
        return javaType.getMutabilityPlan().disassemble(value, null);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) {
        return javaType.getMutabilityPlan().assemble(cached, null);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        Object value = jdbcType.getExtractor(javaType).extract(rs, position, session);
        return value;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException {
        jdbcType.getBinder(javaType).bind(st, value, index, session);
    }

//    @Override
//    public Serializable disassemble(Object value, SharedSessionContract session) {
//        return javaType.getMutabilityPlan().disassemble(value, session);
//    }
//
//    @Override
//    public Object assemble(Serializable cached, SharedSessionContract session) {
//        return javaType.getMutabilityPlan().assemble(cached, null);
//    }

//    static class JsonMutabilityPlan implements MutabilityPlan<Object> {
//
//        @Override
//        public boolean isMutable() {
//            return false;
//        }
//
//        @Override
//        public Object deepCopy(Object value) {
//            return value;
//        }
//
//        @Override
//        public Serializable disassemble(Object value, SharedSessionContract session) {
//            return null;
//        }
//
//        @Override
//        public Object assemble(Serializable cached, SharedSessionContract session) {
//            return null;
//        }
//    }

}
