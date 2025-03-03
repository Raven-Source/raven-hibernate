package org.raven.hibernate.jpa.test.repository;

import org.raven.commons.data.NumberType;
import org.raven.commons.data.annotation.Values;

public class StatusType extends NumberType<Integer> {

    private StatusType(Integer value) {
        super(value);
    }

    /**
     * 处理完成
     */
    public final static StatusType Finish = new StatusType(3);

    /**
     * 处理失败
     */
    public final static StatusType Fail = new StatusType(4);

//    @Create
//    private static StatusType valueOf(Integer i) {
//        return new StatusType(i, "");
//    }

    @Values
    public static StatusType[] values() {
        return new StatusType[]{Finish, Fail};
    }

    public static StatusType valueOf(int value) {
        for (StatusType status : values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
