package org.raven.hibernate.dialect.mysql;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.raven.hibernate.dialect.SQLFunctions;

import java.util.List;

public class MysqlCountDistinctFunction extends StandardSQLFunction {

    public MysqlCountDistinctFunction() {
        super(SQLFunctions.countDistinct, StandardBasicTypes.LONG);
    }

    @Override
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) {

        // 检查参数数量
        if (arguments == null || arguments.isEmpty()) {
            throw new IllegalArgumentException("The function count_distinct() requires exactly 1 arguments.");
        }

        StringBuilder buffer = new StringBuilder();

        buffer.append("count(distinct concat_ws('|'");
        String sep = ", ";
        for (Object arg : arguments) {
            buffer.append(sep);
            buffer.append(arg);
        }
        buffer.append("))");

        return buffer.toString();

    }
}
