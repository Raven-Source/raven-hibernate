package org.raven.hibernate.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.format.FormatMapper;
import org.raven.hibernate.spi.ObjectMapperSupplier;
import org.raven.spring.commons.util.SpringContextUtils;

import java.util.ServiceLoader;

/**
 * date 2022/7/26 11:39
 */
public class JacksonJsonFormatMapper implements FormatMapper {

    public static final JacksonJsonFormatMapper INSTANCE = new JacksonJsonFormatMapper();
    private static ObjectMapperSupplier objectMapperSupplier;

    public static final String SHORT_NAME = "jackson";

    private final ObjectMapper objectMapper;

    static {
        ServiceLoader.load(ObjectMapperSupplier.class).forEach(supplier -> {
            objectMapperSupplier = supplier;
        });
    }

    public JacksonJsonFormatMapper() {
        objectMapper = objectMapperSupplier != null ? objectMapperSupplier.get() : SpringContextUtils.getBean(ObjectMapper.class);
    }

    @Override
    public <T> T fromString(CharSequence charSequence, JavaType<T> javaType, WrapperOptions wrapperOptions) {
        if (javaType.getJavaType() == String.class || javaType.getJavaType() == Object.class) {
            return (T) charSequence.toString();
        }
        try {
            return objectMapper.readValue(charSequence.toString(), objectMapper.constructType(javaType.getJavaType()));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not deserialize string to java type: " + javaType, e);
        }
    }

    @Override
    public <T> String toString(T value, JavaType<T> javaType, WrapperOptions wrapperOptions) {
        if (javaType.getJavaType() == String.class || javaType.getJavaType() == Object.class) {
            return (String) value;
        }
        try {
            return objectMapper.writerFor(objectMapper.constructType(javaType.getJavaType()))
                    .writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not serialize object of java type: " + javaType, e);
        }
    }
}
