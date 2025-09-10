package org.raven.hibernate.entity;

import org.hibernate.annotations.*;
import org.raven.commons.data.Entity;
import org.raven.commons.data.StringType;
import org.raven.commons.data.ValueType;
import org.raven.hibernate.convert.NumberValueType;
import org.raven.hibernate.convert.StringValueType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

/**
 * date 2022/7/26 10:58
 */
@EntityListeners({AuditingEntityListener.class})
@MappedSuperclass
//@TypeRegistrations(value = {
//        @TypeRegistration(basicClass = ValueType.class, userType = NumberValueType.class),
//        @TypeRegistration(basicClass = StringType.class, userType = StringValueType.class),
//})
@DynamicUpdate
public abstract class BaseEntity<TKey> implements Entity<TKey> {

    public static final String EMPTY = "";
    public static final String NONE = "NONE";

//    public static final String DISTRIBUTED_ID_NAME = "distributed-id-gen";
//    public static final String DISTRIBUTED_ID_STRATEGY = "com.raven.hibernate.entity.id.DefaultIdentifierGenerator";

//    public static final String VALUE_TYPE_NAME = "value-type";
//    public static final String STRING_TYPE_NAME = "string-type";
//    public static final String JSON_TYPE_NAME = "json";

    public BaseEntity() {

    }
}
