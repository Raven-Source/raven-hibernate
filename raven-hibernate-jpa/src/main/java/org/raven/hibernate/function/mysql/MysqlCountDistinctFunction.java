package org.raven.hibernate.function.mysql;

import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.spi.TypeConfiguration;
import org.raven.hibernate.dialect.SQLFunctions;

import java.util.List;

public class MysqlCountDistinctFunction extends AbstractSqmSelfRenderingFunctionDescriptor {

    private final String prefix;
    private final String suffix;

    public MysqlCountDistinctFunction(String prefix, String suffix, TypeConfiguration typeConfiguration) {
//        super(SQLFunctions.countDistinct, StandardBasicTypes.LONG);

        super(
                SQLFunctions.countDistinct,
                StandardArgumentsValidators.composite(
                        StandardArgumentsValidators.min(1)
                ),
                StandardFunctionReturnTypeResolvers.invariant(
                        typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.LONG)
                ),
                null
        );

        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public void render(SqlAppender sqlAppender,
                       List<? extends SqlAstNode> arguments,
                       ReturnableType<?> returnType,
                       SqlAstTranslator<?> walker) {

        sqlAppender.append(prefix);
        sqlAppender.append("count(distinct concat_ws('|'");
        for (int i = 0; i < arguments.size(); i++) {
            sqlAppender.append(", ");
            arguments.get(i).accept(walker);
        }
        sqlAppender.append("))");
        sqlAppender.append(suffix);

    }

//    @Override
//    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) {
//
//        // 检查参数数量
//        if (arguments == null || arguments.isEmpty()) {
//            throw new IllegalArgumentException("The function count_distinct() requires exactly 1 arguments.");
//        }
//
//        StringBuilder buffer = new StringBuilder();
//
//        buffer.append("count(distinct concat_ws('|'");
//        String sep = ", ";
//        for (Object arg : arguments) {
//            buffer.append(sep);
//            buffer.append(arg);
//        }
//        buffer.append("))");
//
//        return buffer.toString();
//
//    }
}
