package org.raven.hibernate.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.util.ObjectMapperSupplier;
import org.raven.spring.commons.util.SpringContextUtils;

/**
 * date 2022/7/26 11:39
 */
public class ObjectMapperSupplierImpl implements ObjectMapperSupplier {

    @Override
    public ObjectMapper get() {
        return SpringContextUtils.getBean(ObjectMapper.class);

    }
}
