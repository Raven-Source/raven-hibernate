package org.raven.hibernate.spi;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperSupplier {

    ObjectMapper get();

}
