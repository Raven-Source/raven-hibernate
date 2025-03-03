package org.raven.hibernate.dialect.mysql;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.raven.commons.util.StringUtils;
import org.raven.hibernate.dialect.SQLFunctions;
import org.raven.hibernate.predicate.ArrayHasType;

import java.util.List;

public class MysqlArrayHasFunction extends StandardSQLFunction {

    public MysqlArrayHasFunction() {
        super(SQLFunctions.arrayHas, StandardBasicTypes.BOOLEAN);
    }

    @Override
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) {

        // 检查参数数量
        if (arguments == null || arguments.size() < 2) {
            throw new IllegalArgumentException("The function array_has() requires exactly 2 arguments.");
        }

        String jsonPath = (String) arguments.get(0);
        String jsonValue = (String) arguments.get(1);
        ArrayHasType arrayHasType = null;
        if (arguments.size() > 2) {
            arrayHasType = ArrayHasType.of(Integer.parseInt((String) arguments.get(2)));
        }

        if (StringUtils.isEmpty(jsonPath)) {
            throw new IllegalArgumentException("The first argument (JSON path) cannot be null or empty.");
        }

        if (StringUtils.isEmpty(jsonValue)) {
            throw new IllegalArgumentException("The second argument (JSON value) cannot be null or empty.");
        }

        return String.format("%s(%s -> '$[*]', json_extract(%s, '$[*]'))",
                ArrayHasType.ALL.equals(arrayHasType) ? "json_contains" : "json_overlaps",
                jsonPath,
                jsonValue
        );

    }
}
