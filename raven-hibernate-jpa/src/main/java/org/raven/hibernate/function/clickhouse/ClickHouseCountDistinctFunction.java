package org.raven.hibernate.function.clickhouse;

import org.hibernate.metamodel.model.domain.ReturnableType;
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

public class ClickHouseCountDistinctFunction extends AbstractSqmSelfRenderingFunctionDescriptor {

    private final String prefix;
    private final String suffix;

    public ClickHouseCountDistinctFunction(String prefix, String suffix, TypeConfiguration typeConfiguration) {

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
        sqlAppender.append("uniqExact(tuple(");
        String sep = "";
        for (int i = 0; i < arguments.size(); i++) {
            sqlAppender.append(sep);
            arguments.get(i).accept(walker);
            sep = ", ";
        }
        sqlAppender.append("))");
        sqlAppender.append(suffix);

    }


}
