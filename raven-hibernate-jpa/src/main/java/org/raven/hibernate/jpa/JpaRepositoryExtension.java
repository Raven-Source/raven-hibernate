package org.raven.hibernate.jpa;//package org.raven.hibernate.jpa;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.repository.NoRepositoryBean;
//
//import java.util.List;
//
///**
// * @author yanfeng
// * date 2021.07.06 21:30
// */
//@NoRepositoryBean
//public interface JpaRepositoryExtension<T, ID> extends JpaRepositorySupport<T, ID> {
//
//    //# region deprecated
//
//    /**
//     * @deprecated replaced by <code>findList()</code>
//     */
//    @Deprecated
//    default List<T> list(Filter<T> filter) {
//        return findList(filter);
//    }
//
//    /**
//     * @deprecated replaced by <code>findList()</code>
//     */
//    @Deprecated
//    default List<T> list(Filter<T> filter, Selector<T> selector) {
//        return findList(filter, selector);
//    }
//
//    /**
//     * @deprecated replaced by <code>findList()</code>
//     */
//    @Deprecated
//    default List<T> list(Filter<T> filter, Selector<T> selector, Sorter<T> sort) {
//        return findList(filter, selector, sort);
//    }
//
//    /**
//     * @deprecated replaced by <code>findList()</code>
//     */
//    @Deprecated
//    default List<T> list(FilterExpression<T> filterExpression) {
//        return findList(filterExpression);
//    }
//
//    /**
//     * @deprecated replaced by <code>findList()</code>
//     */
//    @Deprecated
//    default List<T> list(FilterExpression<T> filterExpression,
//                         SelectorExpression<T> selectorExpression) {
//        return findList(filterExpression, selectorExpression);
//    }
//
//    /**
//     * @deprecated replaced by <code>findList()</code>
//     */
//    @Deprecated
//    default List<T> list(FilterExpression<T> filterExpression,
//                         SelectorExpression<T> selectorExpression,
//                         SorterExpression<T> sort) {
//        return findList(filterExpression, selectorExpression, sort);
//    }
//
//    /**
//     * @deprecated replaced by <code>findPage()</code>
//     */
//    @Deprecated
//    default Page<T> page(Filter<T> filter, Pageable pageable) {
//        return findPage(filter, pageable);
//    }
//
//    /**
//     * @deprecated replaced by <code>findPage()</code>
//     */
//    @Deprecated
//    default Page<T> page(Filter<T> filter, Selector<T> selector, Pageable pageable) {
//        return findPage(filter, selector, pageable);
//    }
//
//    /**
//     * @deprecated replaced by <code>findPage()</code>
//     */
//    @Deprecated
//    default <R> Page<R> page(Filter<T> filter, Selector<T> selector, Pageable pageable, Class<R> resultClass) {
//        return findPage(filter, selector, pageable, resultClass);
//    }
//
//    /**
//     * @deprecated replaced by <code>findPage()</code>
//     */
//    @Deprecated
//    default Page<T> page(FilterExpression<T> filterExpression, Pageable pageable) {
//        return findPage(filterExpression, pageable);
//    }
//
//    /**
//     * @deprecated replaced by <code>findPage()</code>
//     */
//    @Deprecated
//    default Page<T> page(FilterExpression<T> filterExpression,
//                         SelectorExpression<T> selectorExpression, Pageable pageable) {
//        return findPage(filterExpression, selectorExpression, pageable);
//    }
//
//    /**
//     * @deprecated replaced by <code>findPage()</code>
//     */
//    @Deprecated
//    default <R> Page<R> page(FilterExpression<T> filterExpression,
//                             SelectorExpression<T> selectorExpression,
//                             Pageable pageable, Class<R> resultClass) {
//        return findPage(filterExpression, selectorExpression, pageable, resultClass);
//    }
//
//    //# endregion
//
//
//    default int update(UpdateSet<T> updateSet, Filter<T> filter) {
//        return update(filter, updateSet);
//    }
//
//    default int update(UpdateSetExpression<T> updateSetExpression, FilterExpression<T> filterExpression) {
//        return update(filterExpression, updateSetExpression);
//    }
//}
