package org.raven.hibernate.jpa.test.repository;

import lombok.Getter;
import org.raven.commons.data.Describable;
import org.raven.commons.data.NumberType;
import org.raven.commons.data.annotation.Create;
import org.raven.commons.data.annotation.Values;

public class UserType extends NumberType<Integer> implements Describable {

    @Getter
    private final String description;

    UserType(Integer value, String desc) {
        super(value);
        this.description = desc;
    }

    public static final UserType NONE = new UserType(0, "无");

    public static final UserType Development = new UserType(1 << 30, "研发");

    public static final UserType Tenant = new UserType(1, "租户");

    public static final UserType Operation = new UserType(16, "平台运营");

    @Values
    public static UserType[] values() {
        return new UserType[]{Development, Tenant, Operation};
    }

    @Create
    private static UserType valueOf(Integer value) {
        return new UserType(value, "");
    }

    @Deprecated
    public static final UserType SALE_TRADER = new UserType(3, "经销客户");

    @Deprecated
    public static final UserType SUPPLIER = new UserType(4, "供应商");

    public boolean contain(UserType other) {
        int i = this.getValue() & other.getValue();
        return i == other.getValue();

    }
}
