package org.raven.hibernate.predicate;

import jakarta.persistence.criteria.Expression;
import org.hibernate.query.sqm.NodeBuilder;
import org.hibernate.query.sqm.SemanticQueryWalker;
import org.hibernate.query.sqm.tree.SqmCopyContext;
import org.hibernate.query.sqm.tree.expression.SqmExpression;
import org.hibernate.query.sqm.tree.expression.SqmFunction;
import org.hibernate.query.sqm.tree.expression.SqmLiteral;
import org.hibernate.query.sqm.tree.predicate.AbstractSqmPredicate;
import org.hibernate.query.sqm.tree.predicate.SqmBooleanExpressionPredicate;
import org.hibernate.query.sqm.tree.predicate.SqmPredicate;
import org.hibernate.type.BasicType;

import java.util.Collection;
import java.util.stream.Collectors;

public class ArrayHasSqmPredicate extends AbstractSqmPredicate {

    private final SqmExpression<?> arrayExpr;
    private final Collection<?> values;
    private final ArrayHasType arrayHasType;
    private final ArrayValueType arrayValueType;

    private final SqmFunction<Boolean> functionExpression;


    /**
     * 构造函数
     *
     * @param arrayExpr      字段表达式
     * @param values         待匹配集合
     * @param arrayHasType   枚举 ALL/ANY
     * @param arrayValueType 枚举 STRING/NUMBER
     * @param nodeBuilder    NodeBuilder
     */
    public ArrayHasSqmPredicate(
            SqmExpression<?> arrayExpr,
            Collection<?> values,
            ArrayHasType arrayHasType,
            ArrayValueType arrayValueType,
            NodeBuilder nodeBuilder
    ) {
        super(nodeBuilder.getBooleanType(), nodeBuilder);

        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values cannot be empty");
        }

        this.arrayExpr = arrayExpr;
        this.values = values;
        this.arrayHasType = arrayHasType;
        this.arrayValueType = arrayValueType;

        this.functionExpression = buildFunctionExpression();
    }

    /**
     * 构造 SqmFunction，使其在 SQL AST 阶段被 Hibernate 正确生成 SQL
     */
    private SqmFunction<Boolean> buildFunctionExpression() {

        NodeBuilder nodeBuilder = nodeBuilder();

        BasicType<String> stringType =
                nodeBuilder.getTypeConfiguration()
                        .getBasicTypeRegistry()
                        .getRegisteredType(String.class);

        SqmLiteral<String> valueLiteral =
                new SqmLiteral<>(buildJsonArray(values), stringType, nodeBuilder);

        SqmLiteral<String> hasTypeLiteral =
                new SqmLiteral<>(arrayHasType.name(),
                        nodeBuilder.getTypeConfiguration().getBasicTypeRegistry().getRegisteredType(String.class),
                        nodeBuilder);

        SqmLiteral<String> valueTypeLiteral =
                new SqmLiteral<>(arrayValueType.name(),
                        nodeBuilder.getTypeConfiguration().getBasicTypeRegistry().getRegisteredType(String.class),
                        nodeBuilder);

        Expression<?>[] args = new Expression<?>[]{
                arrayExpr,
                valueLiteral,
                hasTypeLiteral,
                valueTypeLiteral
        };

        return nodeBuilder.function(
                "array_has",
                Boolean.class,
                args
        );
    }

//    /**
//     * 转成 SQM 表达式的参数
//     */
//    public SqmLiteral<String> toLiteral(NodeBuilder nodeBuilder) {
//
//        return new SqmLiteral<>(buildJsonArray(values),
//                nodeBuilder.getTypeConfiguration().getBasicTypeRegistry().getRegisteredType(String.class),
//                nodeBuilder);
//    }

    /**
     * Collection → JSON 数组字符串
     */
    private String buildJsonArray(Collection<?> values) {
        return values.stream()
                .map(v -> {
                    if (v instanceof CharSequence) {
                        return "\"" + v + "\"";
                    } else if (v instanceof Number) {
                        return v.toString();
                    } else {
                        throw new IllegalArgumentException("Unsupported value type: " + v.getClass());
                    }
                })
                .collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public boolean isNegated() {
        return false;
    }

    @Override
    public SqmPredicate not() {
        // 创建一个取反的谓词
        return new ArrayHasSqmPredicate(
                arrayExpr,
                values,
                arrayHasType,
                arrayValueType,
                nodeBuilder()
        ) {
            @Override
            public boolean isNegated() {
                return true;
            }

            @Override
            public SqmPredicate not() {
                // 双重否定即为原谓词
                return ArrayHasSqmPredicate.this;
            }
        };
    }

    @Override
    public SqmPredicate copy(SqmCopyContext context) {
        final SqmPredicate existing = context.getCopy(this);
        if (existing != null) {
            return existing;
        }

        return context.registerCopy(
                this,
                new ArrayHasSqmPredicate(
                        arrayExpr.copy(context),
                        values,
                        arrayHasType,
                        arrayValueType,
                        nodeBuilder()
                )
        );
    }

    @Override
    public <X> X accept(SemanticQueryWalker<X> walker) {
        return walker.visitBooleanExpressionPredicate(new SqmBooleanExpressionPredicate(functionExpression, nodeBuilder()));
    }

    @Override
    public void appendHqlString(StringBuilder sb) {
        // 构建 HQL 字符串表示
        sb.append("array_has(");
        arrayExpr.appendHqlString(sb);
        sb.append(", ");
        sb.append(buildJsonArray(values));
        sb.append(", ");
        sb.append(arrayHasType.getValue());
        sb.append(", ");
        sb.append(arrayValueType.getValue());
        sb.append(")");
    }
}