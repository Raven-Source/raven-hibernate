package org.raven.hibernate.spi;

import tools.jackson.databind.json.JsonMapper;

public interface JsonMapperSupplier {

    JsonMapper get();

}
