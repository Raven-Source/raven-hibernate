package org.raven.hibernate.convert;

import com.vladmihalcea.hibernate.util.ReflectionUtils;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.annotations.common.reflection.java.JavaXMember;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.usertype.DynamicParameterizedType;
import org.raven.commons.data.SerializableTypeUtils;
import org.raven.commons.data.StringType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Properties;

/**
 * @author yi.liang
 * @since JDK1.8
 * date 2020.06.04 17:23
 */
@SuppressWarnings("unchecked")
public class StringTypeTypeDescriptor
        extends AbstractTypeDescriptor<StringType>
        implements DynamicParameterizedType {

    private Type type;

    public StringTypeTypeDescriptor() {
        super(StringType.class, ImmutableMutabilityPlan.INSTANCE);
    }

    public StringTypeTypeDescriptor(Class<StringType> type) {
        super(type, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(StringType value) {
        return value == null ? "<null>" : value.getValue().toString();
    }

    @Override
    public StringType fromString(String s) {
        return s == null ? null : SerializableTypeUtils.stringValueOf(this.getJavaType(), s);
    }

    @Override
    public <X> X unwrap(StringType t, Class<X> aClass, WrapperOptions wrapperOptions) {
        return t == null ? null : aClass.cast(t.getValue());
    }

    @Override
    public <X> StringType wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        } else {
            return value instanceof String ? SerializableTypeUtils.valueOf(typeToClass(), value) : null;
        }
    }


    @Override
    public boolean areEqual(StringType one, StringType another) {
        if (one == another) {
            return true;
        }
        if (one == null || one.getValue() == null || another == null || another.getValue() == null) {
            return false;
        }
        return one.getValue().equals(another.getValue());
    }

    @Override
    public void setParameterValues(Properties parameters) {
        final XProperty xProperty = (XProperty) parameters.get(DynamicParameterizedType.XPROPERTY);
        if (xProperty instanceof JavaXMember) {
            type = ReflectionUtils.invokeGetter(xProperty, "javaType");
        } else {
            type = ((ParameterType) parameters.get(PARAMETER_TYPE)).getReturnedClass();
        }
    }

    private Class<StringType> typeToClass() {
        Type classType = type;
        if (type instanceof ParameterizedType) {
            classType = ((ParameterizedType) type).getRawType();
        }
        return (Class<StringType>) classType;
    }
}
