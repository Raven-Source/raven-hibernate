package org.raven.hibernate.jpa.test.repository;

import lombok.Getter;
import org.raven.commons.data.Describable;
import org.raven.commons.data.ValueType;

/**
 * @author yi.liang
 * @since JDK1.8
 * date 2021.11.01 17:48
 */
@Getter
public enum Status implements ValueType<Integer>, Describable {

    Finish(3, "处理完成"),
    Fail(4, "处理失败"),
    ;

    private final Integer value;
    private final String description;

    Status(Integer value, String desc) {
        this.value = value;
        this.description = desc;
    }

    public static Status valueOf(int value) {
        for (Status status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
