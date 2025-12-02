package org.raven.hibernate.dialect;

import org.hibernate.boot.model.FunctionContributions;
import org.raven.hibernate.function.clickhouse.ClickHouseArrayHasFunction;
import org.raven.hibernate.function.clickhouse.ClickHouseCountDistinctFunction;

public class ClickhouseDialect extends org.hibernate.dialect.MySQLDialect {

    public ClickhouseDialect() {

        super();

//        registerFunction("castToString", new StandardSQLFunction("castToString", StandardBasicTypes.STRING));
//        registerFunction(SQLFunctions.countDistinct, new ClickHouseCountDistinctFunction());
//        registerFunction(SQLFunctions.arrayHas, new ClickHouseArrayHasFunction());
//        registerFunction(SQLFunctions.castToString, new SQLFunctionTemplate(StandardBasicTypes.STRING, "ifNull(cast(?1 as Nullable(String)), ?2)"));
    }


    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);

        functionContributions.getFunctionRegistry().register(
                SQLFunctions.countDistinct, new ClickHouseCountDistinctFunction("", "", functionContributions.getTypeConfiguration())
        );

        functionContributions.getFunctionRegistry().register(
                SQLFunctions.arrayHas, new ClickHouseArrayHasFunction("", "", functionContributions.getTypeConfiguration())
        );
    }
}
