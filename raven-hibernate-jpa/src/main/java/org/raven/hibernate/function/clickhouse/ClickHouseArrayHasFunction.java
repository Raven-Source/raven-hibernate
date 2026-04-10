package org.raven.hibernate.function.clickhouse;

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
import org.raven.hibernate.predicate.ArrayValueType;

import java.util.List;

public class ClickHouseArrayHasFunction extends AbstractSqmSelfRenderingFunctionDescriptor {

    private final String prefix;
    private final String suffix;

    public ClickHouseArrayHasFunction(String prefix, String suffix, TypeConfiguration typeConfiguration) {
        super(SQLFunctions.arrayHas,
                StandardArgumentsValidators.composite(
                        StandardArgumentsValidators.min(4)
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

        ArrayHasType arrayHasType = ArrayHasType.nameOf(((QueryLiteral<String>) arguments.get(2)).getLiteralValue());
        ArrayValueType arrayValueType = ArrayValueType.nameOf(((QueryLiteral<String>) arguments.get(3)).getLiteralValue());

        if (arrayValueType == null) {
            throw new IllegalArgumentException("The four argument (ArrayValueType) cannot be null or empty.");
        }

        sqlAppender.append(prefix);
        sqlAppender.append(ArrayHasType.ALL.equals(arrayHasType) ? "hasAll" : "hasAny");
        sqlAppender.append("(");
        arguments.get(0).accept(walker);

        sqlAppender.append(", JSONExtract(");
        arguments.get(1).accept(walker);
        sqlAppender.append(", ");
        sqlAppender.append(arrayType(arrayValueType));
        sqlAppender.append("))");

        sqlAppender.append(suffix);
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
