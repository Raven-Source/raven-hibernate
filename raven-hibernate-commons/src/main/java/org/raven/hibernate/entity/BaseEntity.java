package org.raven.hibernate.entity;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.raven.commons.data.Entity;
import org.raven.serializer.hibernate.convert.StringTypeType;
import org.raven.serializer.hibernate.convert.ValueTypeType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

/**
 * date 2022/7/26 10:58
 */
@EntityListeners({AuditingEntityListener.class})
@MappedSuperclass
@TypeDefs({
        @TypeDef(name = BaseEntity.JSON_TYPE_NAME, typeClass = JsonStringType.class),
        @TypeDef(name = BaseEntity.STRING_TYPE_NAME, typeClass = StringTypeType.class),
        @TypeDef(name = BaseEntity.VALUE_TYPE_NAME, typeClass = ValueTypeType.class)
})
@DynamicUpdate
public abstract class BaseEntity<TKey> implements Entity<TKey> {

    public static final String EMPTY = "";
    public static final String NONE = "NONE";

//    public static final String DISTRIBUTED_ID_NAME = "distributed-id-gen";
//    public static final String DISTRIBUTED_ID_STRATEGY = "com.raven.hibernate.entity.id.DefaultIdentifierGenerator";

    public static final String VALUE_TYPE_NAME = "value-type";
    public static final String STRING_TYPE_NAME = "string-type";
    public static final String JSON_TYPE_NAME = "json";

    public BaseEntity() {

    }
}
