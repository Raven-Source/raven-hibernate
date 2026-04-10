package org.raven.hibernate.convert;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.descriptor.java.BasicJavaType;
import org.hibernate.type.spi.TypeConfigurationAware;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;
import org.raven.commons.data.SerializableType;
import org.raven.commons.data.SerializableTypeUtils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

@SuppressWarnings("all")
public abstract class BasicSerializableType<T extends SerializableType<?>>
        extends BasicUserType<T>
        implements DynamicParameterizedType, TypeConfigurationAware
        , UserType<T> {

    protected Class<?> valueClass;
    protected BasicJavaType javaType;

    @Override
    public void setParameterValues(Properties parameters) {

        super.setParameterValues(parameters);
        javaType = (BasicJavaType) typeConfiguration.getJavaTypeRegistry().getDescriptor(valueClass);

    }

    @Override
    protected Class<?> javaTypeForJdbcType(Class<T> javaTypeClass) {
        this.valueClass = SerializableTypeUtils.getGenericType(javaTypeClass);
        return this.valueClass;
    }

    @Override
    public boolean equals(T x, T y) {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        return x.equalsValue(y);
    }

    @Override
    public int hashCode(T x) {
        return x.getValue().hashCode();
    }

    @Override
    public Serializable disassemble(T value) {
        return value == null ? null : (Serializable) value.getValue();
    }

    @Override
    public T assemble(Serializable cached, Object owner) {
        return cached == null ? null : (T) SerializableTypeUtils.valueOf(javaTypeClass, cached);
    }

    @Override
    public T nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        Object value = jdbcType.getExtractor(javaType).extract(rs, position, session);
        return value != null ? SerializableTypeUtils.valueOf(javaTypeClass, value) : null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, T value, int index, SharedSessionContractImplementor session) throws SQLException {
        jdbcType.getBinder(javaType).bind(st, value != null ? value.getValue() : null, index, session);
    }
}
