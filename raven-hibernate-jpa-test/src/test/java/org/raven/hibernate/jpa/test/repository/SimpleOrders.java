package org.raven.hibernate.jpa.test.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import org.raven.commons.data.ValueType;

import static org.raven.hibernate.entity.BaseEntity.VALUE_TYPE_NAME;

/**
 * @author by yanfeng
 * date 2021/9/14 11:53
 */
@Data
@AllArgsConstructor
@FieldNameConstants
public class SimpleOrders {

    private Long id;

    private String name;

    private boolean del;

    @Type(type = VALUE_TYPE_NAME)
    private StatusType status;

    public SimpleOrders() {
    }

    public SimpleOrders(String name) {

        this.name = name;
    }

    public SimpleOrders(Long id, boolean del) {

        this.id = id;
        this.del = del;
    }

    public SimpleOrders(Long id, ValueType<Integer> status) {

        this.id = id;
        this.status = ((StatusType) status);
    }

}
