package org.raven.hibernate.convert;

import com.vladmihalcea.hibernate.type.AbstractHibernateType;
import org.hibernate.type.descriptor.sql.IntegerTypeDescriptor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.raven.commons.data.ValueType;

import java.util.Properties;

/**
 * @author yi.liang
 * @since JDK1.8
 * date 2020.06.04 17:23
 */
public class ValueTypeType extends AbstractHibernateType<ValueType> implements DynamicParameterizedType {

    public static final ValueTypeType INSTANCE = new ValueTypeType();

    public ValueTypeType() {
        super(
            IntegerTypeDescriptor.INSTANCE,
            new ValueTypeTypeDescriptor()
        );
    }

    @Override
    public String getName() {
        return "value-type";
    }

    @Override
    public void setParameterValues(Properties parameters) {

        ((ValueTypeTypeDescriptor) getJavaTypeDescriptor()).setParameterValues(parameters);
    }
}
