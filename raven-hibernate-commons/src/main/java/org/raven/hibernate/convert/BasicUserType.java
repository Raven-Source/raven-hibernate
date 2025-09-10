package org.raven.hibernate.convert;

import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.annotations.common.reflection.java.JavaXMember;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeJavaClassMappings;
import org.hibernate.type.descriptor.jdbc.spi.JdbcTypeRegistry;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.type.spi.TypeConfigurationAware;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Properties;

public abstract class BasicUserType<T> implements DynamicParameterizedType, TypeConfigurationAware, UserType<T> {

    public static final String TYPE = "type";

    protected JdbcType jdbcType;
    protected Class<T> javaTypeClass;

    protected TypeConfiguration typeConfiguration;

    @Override
    public TypeConfiguration getTypeConfiguration() {
        return typeConfiguration;
    }

    @Override
    public void setTypeConfiguration(TypeConfiguration typeConfiguration) {
        this.typeConfiguration = typeConfiguration;
    }

    @Override
    public void setParameterValues(Properties parameters) {

        final XProperty xProperty = (XProperty) parameters.get(DynamicParameterizedType.XPROPERTY);
        if (xProperty instanceof JavaXMember) {

            Type type = ((JavaXMember) xProperty).getJavaType();
            if (type instanceof ParameterizedType parameterizedType) {
                this.javaTypeClass = (Class) parameterizedType.getRawType();
            } else if (type instanceof Class<?>) {
                this.javaTypeClass = (Class) type;
            }

        } else {
            this.javaTypeClass = (Class) ((ParameterType) parameters.get(PARAMETER_TYPE)).getReturnedClass();
        }

        JdbcTypeRegistry jdbcTypeRegistry = this.typeConfiguration.getJdbcTypeRegistry();
        int jdbcTypeCode;
        if (parameters.containsKey(TYPE)) {
            jdbcTypeCode = Integer.parseInt((String) parameters.get(TYPE));
        } else {
            jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass(javaTypeForJdbcType(javaTypeClass));
        }
        this.jdbcType = jdbcTypeRegistry.getDescriptor(jdbcTypeCode);

    }

    protected abstract Class<?> javaTypeForJdbcType(Class<T> javaTypeClass);

    @Override
    public int getSqlType() {
        return jdbcType.getJdbcTypeCode();
    }

    @Override
    public Class<T> returnedClass() {
        return javaTypeClass;
    }

    @Override
    public T deepCopy(T value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

}
