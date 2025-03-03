package org.raven.hibernate.dialect;


import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;
import org.raven.hibernate.dialect.mysql.MysqlArrayHasFunction;
import org.raven.hibernate.dialect.mysql.MysqlCountDistinctFunction;

public class MySQLDialect extends MySQL8Dialect {

    public MySQLDialect() {

        super();

        registerFunction(SQLFunctions.countDistinct, new MysqlCountDistinctFunction());

        registerFunction(SQLFunctions.arrayHas, new MysqlArrayHasFunction());
        registerFunction(SQLFunctions.castToString, new SQLFunctionTemplate(StandardBasicTypes.STRING, "ifNull(cast(?1 as char), ?2)"));
    }


}
