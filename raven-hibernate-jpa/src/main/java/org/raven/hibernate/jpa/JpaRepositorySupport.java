package org.raven.hibernate.jpa;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.FluentQuery;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@NoRepositoryBean
public interface JpaRepositorySupport<T, ID> extends JpaRepositoryImplementation<T, ID> {

    boolean containsAttribute(String attributeName);

    EntityManager entityManager();

    CriteriaQuery<T> criteriaQuery();

    TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery);

    //region query

    T findOne(ID id);

    default T findOne(Filter<T> filter) {
        return findOne(filter, null, null);
    }

    default T findOne(Filter<T> filter, Selector<T> selector) {
        return findOne(filter, selector, null);
    }

    T findOne(Filter<T> filter, Selector<T> selector, Sorter<T> sort);

    <R> R findOne(Filter<T> filter, Selector<T> selector, Sorter<T> sort, Class<R> resultClass);

    default T findOne(FilterExpression<T> filterExpression) {
        return findOne(filterExpression, null, null);
    }

    default T findOne(FilterExpression<T> filterExpression,
                      SelectorExpression<T> selectorExpression) {
        return findOne(filterExpression, selectorExpression, null);
    }

    T findOne(FilterExpression<T> filterExpression,
              SelectorExpression<T> selectorExpression,
              SorterExpression<T> sorterExpression);

    default <R> R findOne(FilterExpression<T> filterExpression,
                          SelectorExpression<T> selectorExpression,
                          SorterExpression<T> sorterExpression,
                          Class<R> resultClass) {

        FindOptions<T> findOptions = FindOptions.<T>empty()
                .filter(filterExpression)
                .select(selectorExpression)
                .sort(sorterExpression);

        return findOne(findOptions, resultClass);
    }

    T findOne(FindOptions<T> findOptions);

    <R> R findOne(FindOptions<T> findOptions, Class<R> resultClass);

    default List<T> findList(Filter<T> filter) {
        return findList(filter, null, null);
    }

    default List<T> findList(Filter<T> filter, Selector<T> selector) {
        return findList(filter, selector, null);
    }

    default List<T> findList(Filter<T> filter, Selector<T> selector, Sorter<T> sort) {
        return findList(filter, selector, sort, 0, 0);
    }

    List<T> findList(Filter<T> filter, Selector<T> selector, Sorter<T> sort, int skip, int limit);

    <R> List<R> findList(Filter<T> filter, Selector<T> selector, Sorter<T> sort, Class<R> resultClass);

    default List<T> findList(FilterExpression<T> filterExpression) {
        return findList(filterExpression, null, null);
    }

    default List<T> findList(FilterExpression<T> filterExpression,
                             SelectorExpression<T> selectorExpression) {
        return findList(filterExpression, selectorExpression, null);
    }

    default List<T> findList(FilterExpression<T> filterExpression,
                             SelectorExpression<T> selectorExpression,
                             SorterExpression<T> sorterExpression) {

        return findList(filterExpression, selectorExpression, sorterExpression, 0, 0);

    }

    default List<T> findList(FilterExpression<T> filterExpression,
                             SelectorExpression<T> selectorExpression,
                             SorterExpression<T> sorterExpression, int skip, int limit) {

        FindOptions<T> findOptions = FindOptions.<T>empty()
                .filter(filterExpression)
                .select(selectorExpression)
                .sort(sorterExpression)
                .skip(skip)
                .limit(limit);

        return findList(findOptions);
    }

    List<T> findList(FindOptions<T> findOptions);

    <R> List<R> findList(FindOptions<T> findOptions, Class<R> resultClass);

    default Page<T> findPage(Filter<T> filter, Pageable pageable) {
        return findPage(filter, null, pageable);
    }

    Page<T> findPage(Filter<T> filter, Selector<T> selector, Pageable pageable);

    <R> Page<R> findPage(Filter<T> filter, Selector<T> selector, Pageable pageable, Class<R> resultClass);

    default Page<T> findPage(FilterExpression<T> filterExpression,
                             Pageable pageable) {
        return findPage(filterExpression, null, pageable);
    }

    Page<T> findPage(FilterExpression<T> filterExpression,
                     SelectorExpression<T> selectorExpression,
                     Pageable pageable);

    <R> Page<R> findPage(FilterExpression<T> filterExpression,
                         SelectorExpression<T> selectorExpression,
                         Pageable pageable, Class<R> resultClass);

    Page<T> findPage(FindOptions<T> findOptions);

    <R> Page<R> findPage(FindOptions<T> findOptions, Class<R> resultClass);

    long count(Filter<T> filter);

    long count(CountOptions<T> countOptions);

//    long countDistinct(Filter<T> filter, SingleExpression<T> expression);

    long count(FilterExpression<T> filterExpression);

//    long countDistinct(FilterExpression<T> filterExpression
//            , SingleExpression<T> expression);

    boolean exists(Filter<T> filter);

    boolean exists(FilterExpression<T> filterExpression);

    //endregion query

    <S extends T> S insert(S entity);

    <S extends T> List<S> insertBatch(Iterable<S> entities);

    int update(Filter<T> filter, UpdateSet<T> updateSet);

    int update(FilterExpression<T> filterExpression, UpdateSetExpression<T> updateSetExpression);

    int updateById(ID id, UpdateSetExpression<T> updateSetExpression);

    int delete(Filter<T> filter);

    int delete(FilterExpression<T> filterExpression);

    //region CrudRepository

    void deleteAllById(Iterable<? extends ID> ids);

    //endregion

    //region JpaRepository

    @Override
    default T getById(ID id) {
        return findOne(id);
    }

    @Override
    default T getReferenceById(ID id) {
        return findOne(id);
    }

    void deleteAllInBatch(Iterable<T> entities);

    void deleteAllByIdInBatch(Iterable<ID> ids);

    <S extends T> List<S> saveAllAndFlush(Iterable<S> entities);

    //endregion

    void flush();

    //region nonsupport QueryByExampleExecutor

    @Override
    default <S extends T> boolean exists(Example<S> example) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default <S extends T> long count(Example<S> example) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default <S extends T> Optional<S> findOne(Example<S> example) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default <S extends T> List<S> findAll(Example<S> example) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default <S extends T> List<S> findAll(Example<S> example, org.springframework.data.domain.Sort sort) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default <S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new RuntimeException("nonsupport");
    }

    //endregion

    //region nonsupport JpaSpecificationExecutor

    @Override
    default Optional<T> findOne(Specification<T> spec) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default List<T> findAll(Specification<T> spec) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default Page<T> findAll(Specification<T> spec, Pageable pageable) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default List<T> findAll(Specification<T> spec, org.springframework.data.domain.Sort sort) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default long count(Specification<T> spec) {
        throw new RuntimeException("nonsupport");
    }

    //endregion

    //region PagingAndSortingRepository nonsupport

    @Override
    default List<T> findAll(org.springframework.data.domain.Sort sort) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default Page<T> findAll(Pageable pageable) {
        throw new RuntimeException("nonsupport");
    }

    @Override
    default boolean exists(Specification<T> spec) {
        throw new RuntimeException("nonsupport");
    }

    //    <S extends T> void insertBigBatch(Iterable<S> entities);

    //endregion

}
