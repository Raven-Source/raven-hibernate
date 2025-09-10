//package org.raven.hibernate.function;
//
//import jakarta.persistence.criteria.CriteriaBuilder;
//import org.checkerframework.checker.nullness.qual.Nullable;
//import org.hibernate.query.sqm.NodeBuilder;
//import org.hibernate.query.sqm.SemanticQueryWalker;
//import org.hibernate.query.sqm.SqmExpressible;
//import org.hibernate.query.sqm.function.SqmFunctionDescriptor;
//import org.hibernate.query.sqm.internal.SqmCriteriaNodeBuilder;
//import org.hibernate.query.sqm.tree.AbstractSqmNode;
//import org.hibernate.query.sqm.tree.SqmCopyContext;
//import org.hibernate.query.sqm.tree.SqmTypedNode;
//import org.hibernate.query.sqm.tree.expression.SqmDistinct;
//import org.hibernate.query.sqm.tree.expression.SqmExpression;
//import org.hibernate.query.sqm.tree.expression.SqmFunction;
//import org.hibernate.type.BasicType;
//import org.raven.hibernate.dialect.SQLFunctions;
//
//import jakarta.persistence.criteria.Expression;
//
//import java.util.List;
//
//public class CountDistinctNodeBuilder {
//
//    public static final String NAME = SQLFunctions.countDistinct;
//
//    private SqmCriteriaNodeBuilder criteriaBuilder;
//
//    public CountDistinctNodeBuilder(CriteriaBuilder criteriaBuilder) {
//
//        this.criteriaBuilder = (SqmCriteriaNodeBuilder) criteriaBuilder;
//    }
//
//    public SqmExpression<Long> countDistinct(List<Expression<?>> argumentExpressions) {
//        return getFunctionDescriptor(NAME).generateSqmExpression(
//                argumentExpressions.stream().map(x -> new SqmCountDistinct<>((SqmExpression<?>) x, criteriaBuilder)).toList(),
//                null,
//                criteriaBuilder.getQueryEngine()
//        );
//
//
//        return getFunctionTemplate( NAME, resultType ).generateSqmExpression(
//                expressionList( args ),
//                resultType,
//                getQueryEngine()
//        );
//    }
//
//
//    private SqmFunctionDescriptor getFunctionDescriptor(String name) {
//        return criteriaBuilder.getQueryEngine().getSqmFunctionRegistry().findFunctionDescriptor(name);
//    }
//
//    public static class SqmCountDistinct<T> extends SqmDistinct<T> implements SqmTypedNode<T> {
//
//
//        private final SqmExpression<T> expression;
//
//        protected SqmCountDistinct(SqmExpression<T> argument, NodeBuilder builder) {
//            super(argument, builder);
//            this.expression = argument;
//        }
//
//        @Override
//        public SqmCountDistinct<T> copy(SqmCopyContext context) {
//            final SqmCountDistinct<T> existing = context.getCopy(this);
//            if (existing != null) {
//                return existing;
//            }
//            return context.registerCopy(
//                    this,
//                    new SqmCountDistinct<>(
//                            expression.copy(context),
//                            nodeBuilder()
//                    )
//            );
//        }
//
//        @Override
//        public void appendHqlString(StringBuilder sb) {
//            expression.appendHqlString(sb);
//        }
//    }
//
//}
