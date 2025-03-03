package org.raven.hibernate.dialect;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;
import org.raven.hibernate.dialect.clickhouse.ClickHouseArrayHasFunction;
import org.raven.hibernate.dialect.clickhouse.ClickHouseCountDistinctFunction;

public class ClickhouseDialect extends MySQL8Dialect {

    public ClickhouseDialect() {

        super();

//        registerFunction("castToString", new StandardSQLFunction("castToString", StandardBasicTypes.STRING));
        registerFunction(SQLFunctions.countDistinct, new ClickHouseCountDistinctFunction());
        registerFunction(SQLFunctions.arrayHas, new ClickHouseArrayHasFunction());
        registerFunction(SQLFunctions.castToString, new SQLFunctionTemplate(StandardBasicTypes.STRING, "ifNull(cast(?1 as Nullable(String)), ?2)"));
    }
}
