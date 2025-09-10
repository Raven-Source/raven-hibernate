package org.raven.hibernate.dialect;


import org.hibernate.boot.model.FunctionContributions;
import org.raven.hibernate.dialect.mysql.MysqlCountDistinctFunction;

public class MySQLDialect extends org.hibernate.dialect.MySQLDialect {

    public MySQLDialect() {

        super();

//        registerFunction(SQLFunctions.countDistinct, new MysqlCountDistinctFunction());
//
//        registerFunction(SQLFunctions.arrayHas, new MysqlArrayHasFunction());
//        registerFunction(SQLFunctions.castToString, new SQLFunctionTemplate(StandardBasicTypes.STRING, "ifNull(cast(?1 as char), ?2)"));
    }


    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);

        functionContributions.getFunctionRegistry().register(
                SQLFunctions.countDistinct, new MysqlCountDistinctFunction("", "", functionContributions.getTypeConfiguration())
        );
    }
}
