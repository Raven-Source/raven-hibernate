package org.raven.hibernate.type;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.format.FormatMapper;
import org.raven.hibernate.spi.JsonMapperSupplier;
import org.raven.spring.commons.util.SpringContextUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.ServiceLoader;

/**
 * date 2022/7/26 11:39
 */
public class JacksonJsonFormatMapper implements FormatMapper {

    public static final JacksonJsonFormatMapper INSTANCE = new JacksonJsonFormatMapper();
    private static JsonMapperSupplier jsonMapperSupplier;

    public static final String SHORT_NAME = "jackson";

    private volatile JsonMapper jsonMapper = null;

    static {
        ServiceLoader.load(JsonMapperSupplier.class).forEach(supplier -> {
            jsonMapperSupplier = supplier;
        });
    }

    public JacksonJsonFormatMapper() {
    }

    private JsonMapper getJsonMapper() {
        if (jsonMapper == null) {
            synchronized (this) {
                if (jsonMapper == null) {
                    jsonMapper = jsonMapperSupplier != null ? jsonMapperSupplier.get() : SpringContextUtils.getBean(JsonMapper.class);
                }
            }
        }
        return jsonMapper;
    }

    @Override
    public <T> T fromString(CharSequence charSequence, JavaType<T> javaType, WrapperOptions wrapperOptions) {
        if (javaType.getJavaType() == String.class || javaType.getJavaType() == Object.class) {
            return (T) charSequence.toString();
        }
        try {
            return getJsonMapper().readValue(charSequence.toString(), getJsonMapper().constructType(javaType.getJavaType()));
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Could not deserialize string to java type: " + javaType, e);
        }
    }

    @Override
    public <T> String toString(T value, JavaType<T> javaType, WrapperOptions wrapperOptions) {
        if (javaType.getJavaType() == String.class || javaType.getJavaType() == Object.class) {
            return (String) value;
        }
        try {
            return getJsonMapper().writerFor(getJsonMapper().constructType(javaType.getJavaType()))
                    .writeValueAsString(value);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Could not serialize object of java type: " + javaType, e);
        }
    }
}
