package org.raven.hibernate.convert;

import com.vladmihalcea.hibernate.type.AbstractHibernateType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.raven.commons.data.StringType;

import java.util.Properties;

/**
 * @author yi.liang
 * @since JDK1.8
 * date 2020.06.04 17:23
 */
public class StringTypeType extends AbstractHibernateType<StringType> implements DynamicParameterizedType {

    public static final StringTypeType INSTANCE = new StringTypeType();

    public StringTypeType() {
        super(
            VarcharTypeDescriptor.INSTANCE,
            new StringTypeTypeDescriptor()
        );
    }

    @Override
    public String getName() {
        return "string-type";
    }

    @Override
    public void setParameterValues(Properties parameters) {

        ((StringTypeTypeDescriptor) getJavaTypeDescriptor()).setParameterValues(parameters);
    }
}
