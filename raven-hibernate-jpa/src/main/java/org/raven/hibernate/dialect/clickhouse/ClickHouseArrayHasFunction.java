package org.raven.hibernate.dialect.clickhouse;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.raven.commons.util.StringUtils;
import org.raven.hibernate.dialect.SQLFunctions;
import org.raven.hibernate.predicate.ArrayHasType;
import org.raven.hibernate.predicate.ArrayValueType;

import java.util.List;

public class ClickHouseArrayHasFunction extends StandardSQLFunction {

    public ClickHouseArrayHasFunction() {
        super(SQLFunctions.arrayHas, StandardBasicTypes.BOOLEAN);
    }

    @Override
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) {

        // 检查参数数量
        if (arguments == null || arguments.size() < 4) {
            throw new IllegalArgumentException("The function array_has() requires exactly 4 arguments.");
        }

        String jsonPath = (String) arguments.get(0);
        String jsonValue = (String) arguments.get(1);
        ArrayHasType arrayHasType = ArrayHasType.of(Integer.parseInt((String) arguments.get(2)));
        ArrayValueType arrayValueType = ArrayValueType.of(Integer.parseInt((String) arguments.get(3)));

        if (StringUtils.isEmpty(jsonPath)) {
            throw new IllegalArgumentException("The first argument (JSON path) cannot be null or empty.");
        }

        if (StringUtils.isEmpty(jsonValue)) {
            throw new IllegalArgumentException("The second argument (JSON value) cannot be null or empty.");
        }

        if (arrayValueType == null) {
            throw new IllegalArgumentException("The four argument (ArrayValueType) cannot be null or empty.");
        }

        return String.format("%s(%s, JSONExtract(%s, %s))",
                ArrayHasType.ALL.equals(arrayHasType) ? "hasAll" : "hasAny",
                jsonPath,
                jsonValue,
                arrayType(arrayValueType)
        );

    }

    private String arrayType(ArrayValueType arrayValueType) {
        switch (arrayValueType) {
            case STRING_ARRAY:
                return "'Array(String)'";
            case INT8_ARRAY:
                return "'Array(Int8)'";
            case INT16_ARRAY:
                return "'Array(Int16)'";
            case INT32_ARRAY:
                return "'Array(Int32)'";
            case INT64_ARRAY:
                return "'Array(Int64)'";

            default:
                throw new IllegalArgumentException("Unsupported array type: " + arrayValueType);
        }
    }
}
