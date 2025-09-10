//package org.raven.hibernate.convert;
//
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//import org.raven.commons.data.SerializableTypeUtils;
//import org.raven.commons.data.ValueType;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//
//
//@Converter(
//        autoApply = true
//)
//public class ValueTypeConverter<N extends Number, T extends ValueType<N>>
//        implements AttributeConverter<T, N> {
//
//
//    private final Class<T> javaType;
//    private final Class<N> jdbcType;
//
//    @SuppressWarnings("unchecked")
//    public ValueTypeConverter() {
//
//        // 解析 <N, T> 的实际类型参数
//        Type[] args = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
//
//        this.jdbcType = (Class<N>) args[0];
//        this.javaType = (Class<T>) args[1];
//
//    }
//
//
//    @Override
//    public N convertToDatabaseColumn(T attribute) {
//        return attribute == null ? null : attribute.getValue();
//    }
//
//    @Override
//    public T convertToEntityAttribute(N dbData) {
//        return SerializableTypeUtils.valueOf(javaType, dbData);
//    }
//}
