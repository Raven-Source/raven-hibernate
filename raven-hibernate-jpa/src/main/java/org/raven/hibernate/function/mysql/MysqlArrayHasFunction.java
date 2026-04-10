package org.raven.hibernate.function.mysql;

import org.hibernate.metamodel.model.domain.ReturnableType;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.QueryLiteral;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.spi.TypeConfiguration;
import org.raven.hibernate.dialect.SQLFunctions;
import org.raven.hibernate.predicate.ArrayHasType;

import java.util.List;

public class MysqlArrayHasFunction extends AbstractSqmSelfRenderingFunctionDescriptor {

    private final String prefix;
    private final String suffix;

    public MysqlArrayHasFunction(String prefix, String suffix, TypeConfiguration typeConfiguration) {
        super(SQLFunctions.arrayHas,
                StandardArgumentsValidators.composite(
                        StandardArgumentsValidators.min(2)
                ),
                StandardFunctionReturnTypeResolvers.invariant(
                        typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.BOOLEAN)
                ),
                null);

        this.prefix = prefix;
        this.suffix = suffix;
    }


    @Override
    public void render(SqlAppender sqlAppender,
                       List<? extends SqlAstNode> arguments,
                       ReturnableType<?> returnType,
                       SqlAstTranslator<?> walker) {

        ArrayHasType arrayHasType = ArrayHasType.ALL;
        if (arguments.size() > 2) {
            arrayHasType = ArrayHasType.nameOf(((QueryLiteral<String>) arguments.get(2)).getLiteralValue());
        }

        sqlAppender.append(prefix);
        sqlAppender.append(ArrayHasType.ALL.equals(arrayHasType) ? "json_contains" : "json_overlaps");
        sqlAppender.append("(");
        arguments.get(0).accept(walker);

        sqlAppender.append("-> '$[*]', json_extract(");
        arguments.get(1).accept(walker);
        sqlAppender.append(", '$[*]'))");

        sqlAppender.append(suffix);
    }

//    @Override
//    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) {
//
//        // 检查参数数量
//        if (arguments == null || arguments.size() < 2) {
//            throw new IllegalArgumentException("The function array_has() requires exactly 2 arguments.");
//        }
//
//        String jsonPath = (String) arguments.get(0);
//        String jsonValue = (String) arguments.get(1);
//        ArrayHasType arrayHasType = null;
//        if (arguments.size() > 2) {
//            arrayHasType = ArrayHasType.of(Integer.parseInt((String) arguments.get(2)));
//        }
//
//        if (StringUtils.isEmpty(jsonPath)) {
//            throw new IllegalArgumentException("The first argument (JSON path) cannot be null or empty.");
//        }
//
//        if (StringUtils.isEmpty(jsonValue)) {
//            throw new IllegalArgumentException("The second argument (JSON value) cannot be null or empty.");
//        }
//
//        return String.format("%s(%s -> '$[*]', json_extract(%s, '$[*]'))",
//                ArrayHasType.ALL.equals(arrayHasType) ? "json_contains" : "json_overlaps",
//                jsonPath,
//                jsonValue
//        );
//
//    }
}
