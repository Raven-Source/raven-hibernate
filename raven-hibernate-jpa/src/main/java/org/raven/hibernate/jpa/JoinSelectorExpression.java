package org.raven.hibernate.jpa;//package org.raven.hibernate.jpa;
//
//import javax.persistence.criteria.Selection;
//import java.util.List;
//import java.util.function.Function;
//
//@FunctionalInterface
//public interface JoinSelectorExpression<S, T> extends Function<SelectorBuilder<S, T>, List<Selection<?>>> {
//    default Selector<T> toSelector() {
//        return (from, builder) -> apply(new SelectorBuilder<>(from, builder));
//    }
//}
