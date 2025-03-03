package org.raven.hibernate.jpa.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.raven.commons.data.Deletable;
import org.raven.commons.util.CollectionUtils;
import org.raven.commons.util.Lists;
import org.raven.commons.util.NumberUtils;
import org.raven.commons.util.StringUtils;
import org.raven.hibernate.entity.listeners.EntityInterceptor;
import org.raven.hibernate.jpa.*;
import org.raven.hibernate.util.ManagedTypeUtils;
import org.raven.hibernate.util.PredicateUtils;
import org.raven.hibernate.util.TupleUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.repository.query.QueryUtils.*;

/**
 * date 2022/7/26 11:53
 */
@Transactional(readOnly = true)
@Slf4j
public class JpaRepositoryImpl<T, ID> //extends SimpleJpaRepository<T, ID>
        implements JpaRepositorySupport<T, ID>, JpaRepositoryImplementation<T, ID> {

    private static final Map<String, Object> hints = new HashMap<>();

    private final EntityManager entityManager;
    private final EntityInformation<T, ID> entityInformation;
    private final PersistenceProvider provider;

    @Nullable
    private CrudMethodMetadata metadata;
//    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

    private final int MAX_LIMIT;
    private final int MAX_SKIP;
//    private final int BATCH_SIZE;

    static {
//        hints.put("javax.persistence.cache.retrieveMode ", CacheRetrieveMode.BYPASS);
        hints.put("javax.persistence.cache.storeMode", CacheStoreMode.BYPASS);
    }

    /**
     * @param jpaEntityInformation {@link JpaEntityInformation}
     * @param entityManager        {@link EntityManager}
     */
    public JpaRepositoryImpl(@NonNull JpaEntityInformation<T, ID> jpaEntityInformation
            , @NonNull EntityManager entityManager) {

        ;
        this.entityManager = entityManager;
        this.provider = PersistenceProvider.fromEntityManager(entityManager);
        this.entityInformation = new DefaultEntityInformation<>(entityManager, jpaEntityInformation);

        int value;

        value = NumberUtils.toInt(getProperty(entityManager, "hibernate.jdbc.max_limit"));
        if (value > 0) {
            this.MAX_LIMIT = value;
        } else {
            this.MAX_LIMIT = 1000;
        }

        value = NumberUtils.toInt(getProperty(entityManager, "hibernate.jdbc.max_skip"));
        if (value > 0) {
            this.MAX_SKIP = value;
        } else {
            this.MAX_SKIP = 100000;
        }

    }

    @SuppressWarnings("unchecked")
    public JpaRepositoryImpl(Class<T> domainClass, EntityManager entityManager, SessionFactory sessionFactory) {
        this((JpaEntityInformation<T, ID>) JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager)
                , entityManager);
    }

    private Object getProperty(EntityManager entityManager, Object key) {
        return entityManager.getEntityManagerFactory().getProperties().get(key);
    }

    protected Class<T> getEntityType() {
        return entityInformation.getJavaType();
    }

    @Override
    public EntityManager entityManager() {
        return entityManager;
    }

    @Override
    public CriteriaQuery<T> criteriaQuery() {
        return entityManager.getCriteriaBuilder().createQuery(getEntityType());
    }

    @Override
    public TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return entityManager.createQuery(criteriaQuery);
    }

    //region query

    //region findOne

    @Override
    public T findOne(ID id) {
        T entity = findOne((root, builder) -> builder.equal(root.get(entityInformation.getIdAttribute()), id));
        return entity;
    }

    @Override
    public T findOne(Filter<T> filter, Selector<T> selector, Sorter<T> sorter) {
        return findOne(filter, selector, sorter, getEntityType());
    }

    @Override
    public <R> R findOne(Filter<T> filter, Selector<T> selector, Sorter<T> sorter, Class<R> resultClass) {

        List<R> result = doFind(filter, selector, null, null, null, sorter, 0, 1, resultClass);
        return !result.isEmpty() ? result.get(0) : null;
    }

    @Override
    public T findOne(FilterExpression<T> filterBuilder,
                     SelectorExpression<T> selectorExpression,
                     SorterExpression<T> sorterExpression) {
        return findOne(filterBuilder, selectorExpression, sorterExpression, getEntityType());
    }

    @Override
    public T findOne(@NonNull FindOptions<T> findOptions) {
        return findOne(findOptions, getEntityType());
    }

    @Override
    public <R> R findOne(@NonNull FindOptions<T> findOptions, Class<R> resultClass) {

        List<R> result = doFind(findOptions, resultClass);
        return !result.isEmpty() ? result.get(0) : null;

    }

    @Deprecated
    @Override
    public Optional<T> findById(ID id) {
        T entity = findOne(id);
        return Optional.ofNullable(entity);
    }

    @Deprecated
    @Override
    public T getOne(ID id) {
        return findOne(id);
    }


    //endregion

    //region findList


    @Override
    public List<T> findAllById(@NonNull Iterable<ID> ids) {

        if (!ids.iterator().hasNext()) {
            return Lists.newArrayList();
        }

        return findList((root, builder) -> idsCriteria(builder, root, ids));

    }

    @Override
    public List<T> findAll() {
        return findList((Filter<T>) null);
    }

    @Override
    public List<T> findList(Filter<T> filter, Selector<T> selector, Sorter<T> sorter, int skip, int limit) {
        return doFind(filter, selector, null, null, null, sorter, skip, limit, getEntityType());
    }

    @Override
    public <R> List<R> findList(Filter<T> filter, Selector<T> selector, Sorter<T> sorter, Class<R> resultClass) {
        return doFind(filter, selector, null, null, null, sorter, 0, 0, resultClass);
    }

    @Override
    public List<T> findList(FindOptions<T> findOptions) {
        return findList(findOptions, getEntityType());
    }

    @Override
    public <R> List<R> findList(FindOptions<T> findOptions, Class<R> resultClass) {
        return doFind(findOptions, resultClass);
    }

    //endregion

    //region findPage

    @Override
    public Page<T> findPage(Filter<T> filter, Selector<T> selector, Pageable pageable) {
        return findPage(filter, selector, pageable, getEntityType());
    }


    @Override
    public <R> Page<R> findPage(Filter<T> filter, Selector<T> selector, Pageable pageable, Class<R> resultClass) {

        long total = count(filter);
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
//            return PageImpl.of(new ArrayList<>(), total, pageable.getSize(), pageable.getPage());
        }

        int skip = (pageable.getPageNumber()) * pageable.getPageSize();
        int limit = pageable.getPageSize();
        Sorter<T> sort = (root, builder) -> SorterBuilder.empty(root, builder).by(pageable.getSort()).build();
        List<R> list = doFind(filter, selector, null, null, null, sort, skip, limit, resultClass);

        return new PageImpl<>(list, pageable, total);
    }


    @Override
    public Page<T> findPage(FilterExpression<T> filterExpression,
                            SelectorExpression<T> selectorExpression,
                            Pageable pageable) {
        return findPage(filterExpression, selectorExpression, pageable, getEntityType());
    }

    @Override
    public <R> Page<R> findPage(FilterExpression<T> filterExpression,
                                SelectorExpression<T> selectorExpression,
                                Pageable pageable, Class<R> resultClass) {

        Filter<T> filter = filterExpression != null ? filterExpression.toFilter() : null;
        Selector<T> selector = selectorExpression != null ? selectorExpression.toSelector() : null;

        return findPage(filter, selector, pageable, resultClass);
    }

    @Override
    public Page<T> findPage(FindOptions<T> findOptions) {
        return findPage(findOptions, getEntityType());
    }

    @Override
    public <R> Page<R> findPage(FindOptions<T> findOptions, Class<R> resultClass) {

        long total = doCount(findOptions.filter(), findOptions.groupBy());
        if (total == 0) {
            return new PageImpl<R>(new ArrayList<>());
        }

        Pageable pageable = PageRequest.of(0, findOptions.limit());

        List<R> list = doFind(findOptions, resultClass);
        return new PageImpl<>(list, pageable, total);
//        return new PageImpl<R>(new ArrayList<>());
    }

    //endregion


    @Override
    public long count() {
        return entityManager.createQuery(getCountQueryString(), Long.class).getSingleResult();
    }


    @Override
    public long count(@NonNull FilterExpression<T> filterExpression) {

        Filter<T> filter = null;

        filter = (root, builder) -> filterExpression.apply(new FilterBuilder<>(root, builder));

        return count(filter);
    }


    @Override
    public long count(@NonNull Filter<T> filter) {
        return doCount(filter, null);
    }


    @Override
    public long count(CountOptions<T> countOptions) {
        return doCount(countOptions.filter(), countOptions.groupBy());
    }


    @Override
    public boolean existsById(ID id) {
        return count((root, builder) -> builder.equal(root.get(entityInformation.getIdAttribute()), id)) > 0;
    }

    @Override
    public boolean exists(Filter<T> filter) {

        TypedQuery<?> query = entityManager.createQuery(buildQuery(filter,
                (root, builder) -> Collections.singletonList(
                        root.get(entityInformation.getIdAttribute())),
                null,
                null,
                null,
                null,
                entityInformation.getIdAttribute().getJavaType()));

        disableCache(query);

        List<?> result = query.setMaxResults(1).getResultList();
        boolean res = result.size() > 0;
        entityManager.clear();
        return res;
    }

    @Override
    public boolean exists(FilterExpression<T> filterExpression) {

        Filter<T> filter = filterExpression.toFilter();

        return exists(filter);
    }

    //endregion

    //region insert


    @Override
    @Transactional
    public <S extends T> S insert(@NonNull S entity) {

        callGlobalInterceptors(PrePersist.class, entity, null, null);

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }


    @Override
    @Transactional
    public <S extends T> List<S> insertBatch(@NonNull Iterable<S> entities) {

        List<S> result = new ArrayList<S>();

        for (S entity : entities) {

            callGlobalInterceptors(PrePersist.class, entity, null, null);

            entityManager.persist(entity);
            result.add(entity);
        }

        entityManager.flush();
        entityManager.clear();

        return result;
    }

    //endregion

    //region save


    @Override
    @Transactional
    public <S extends T> S save(@NonNull S entity) {

        callGlobalInterceptors(PrePersist.class, entity, null, null);

        if (entityInformation.isNew(entity)) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }


    @Override
    @Transactional
    public <S extends T> List<S> saveAll(@NonNull Iterable<S> entities) {

        List<S> result = new ArrayList<S>();

        for (S entity : entities) {

            callGlobalInterceptors(PrePersist.class, entity, null, null);

            result.add(save(entity));
        }

        return result;
    }


    @Override
    @Transactional
    public void flush() {
        entityManager.flush();
    }

    @Override
    @Transactional
    public <S extends T> S saveAndFlush(@NonNull S entity) {

        S result = save(entity);
        flush();

        return result;
    }

    @Override
    @Transactional
    public <S extends T> List<S> saveAllAndFlush(@NonNull Iterable<S> entities) {

        List<S> result = saveAll(entities);
        flush();

        return result;
    }

    //endregion

    //region update

    @Override
    @Transactional
    public int update(FilterExpression<T> filterExpression, @NonNull UpdateSetExpression<T> updateSetExpression) {

        Filter<T> filter = filterExpression != null ? filterExpression.toFilter() : null;
        UpdateSet<T> updateSet = updateSetExpression.toUpdateSet();

        return update(filter, updateSet);
    }

    @Override
    @Transactional
    public int update(@NonNull Filter<T> filter, @NonNull UpdateSet<T> updateSet) {

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaUpdate<T> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(getEntityType());
        Root<T> root = criteriaUpdate.from(getEntityType());

        updateSet.toSet(criteriaUpdate, root, criteriaBuilder);
        criteriaUpdate.where(filter.toPredicate(root, criteriaBuilder));

        employDefaultCriteria(criteriaBuilder, root, criteriaUpdate
                , filter.ignoreTenant(), filter.ignoreDeletable());

        callGlobalInterceptors(PreUpdate.class, null, criteriaUpdate, criteriaBuilder);

        return entityManager.createQuery(criteriaUpdate).executeUpdate();

    }

    @Override
    public int updateById(@NonNull ID id, UpdateSetExpression<T> updateSetExpression) {

        Filter<T> filter = (root, builder) -> builder.equal(root.get(entityInformation.getIdAttribute()), id);
        UpdateSet<T> updateSet = updateSetExpression.toUpdateSet();
        return update(filter, updateSet);
    }

    //endregion

    //region delete

    @Override
    @Transactional
    public int delete(@NonNull FilterExpression<T> filterExpression) {

        Filter<T> filter = null;

        filter = (root, builder) -> filterExpression.apply(new FilterBuilder<>(root, builder));

        return delete(filter);
    }


    @Override
    @Transactional
    public int delete(@NonNull Filter<T> filter) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaDelete<T> criteriaDelete = criteriaBuilder.createCriteriaDelete(getEntityType());
        Root<T> root = criteriaDelete.from(getEntityType());

        criteriaDelete.where(filter.toPredicate(root, criteriaBuilder));

        if (!filter.ignoreTenant()) {
            employTenant(criteriaBuilder, root, criteriaDelete);
        }

        return entityManager.createQuery(criteriaDelete).executeUpdate();
    }

    @Override
    @Transactional
    public void deleteById(@NonNull ID id) {

        delete((root, builder) -> builder.equal(root.get(entityInformation.getIdAttribute()), id));
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void delete(@NonNull T entity) {

        delete((root, builder) -> builder.equal(root.get(entityInformation.getIdAttribute()), entityInformation.getId(entity)));
    }

    @Override
    @Transactional
    public void deleteAllById(@NonNull Iterable<? extends ID> ids) {

        if (!ids.iterator().hasNext()) {
            return;
        }

        delete((root, builder) -> idsCriteria(builder, root, ids));
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void deleteAll(Iterable<? extends T> entities) {
        List<ID> ids = new ArrayList<>();
        entities.forEach(e -> {
            ids.add((ID) entityInformation.getId(e));
        });

        deleteAllById(ids);
    }

    @Override
    @Transactional
    public void deleteAll() {
        entityManager.createQuery(getDeleteAllQueryString()).executeUpdate();
    }

    @Override
    @Transactional
    public void deleteInBatch(Iterable<T> iterable) {
        deleteAllInBatch(iterable);
    }

    @Override
    @Transactional
    public void deleteAllInBatch(@NonNull Iterable<T> entities) {

        if (!entities.iterator().hasNext()) {
            return;
        }

        applyAndBind(getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName()), entities, entityManager)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void deleteAllByIdInBatch(@NonNull Iterable<ID> ids) {

        deleteAllById(ids);
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        deleteAll();
    }

    //endregion


    protected void disableCache(TypedQuery<?> query) {

        for (Map.Entry<String, Object> entry : hints.entrySet()) {
            query.setHint(entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    protected <R> CriteriaQuery<R> buildQuery(
            Filter<T> filter
            , Selector<T> selector
            , GroupBy<T> groupBy, Filter<T> having
            , Joiner<T> joiner
            , Sorter<T> sort
            , Class<R> resultClass
    ) {

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<R> criteriaQuery = null;
        boolean isTuple = Tuple.class.isAssignableFrom(resultClass);
        boolean isMap = Map.class.isAssignableFrom(resultClass);

        if (isTuple || isMap) {
            criteriaQuery = (CriteriaQuery<R>) criteriaBuilder.createTupleQuery();
        } else {
            criteriaQuery = criteriaBuilder.createQuery(resultClass);
        }

        Root<T> root = criteriaQuery.from(getEntityType());
//        CriteriaQueryRoot<T> combineRoot = new CriteriaQueryRootCombine<>(criteriaQuery, root);

        // join operate must before where
        if (joiner != null) {
            List<Join<T, ?>> joinSelections = joiner.join(root, criteriaBuilder);
//            if (joinSelections != null && !joinSelections.isEmpty()) {
//                selections.addAll(joinSelections);
//            }
        }

        boolean ignoreTenant = false;
        boolean ignoreDeletable = false;

        if (filter != null) {
            criteriaQuery = criteriaQuery.where(filter.toPredicate(root, criteriaBuilder));
            ignoreTenant = filter.ignoreTenant();
            ignoreDeletable = filter.ignoreDeletable();
        }
        employDefaultCriteria(criteriaBuilder, root, criteriaQuery
                , ignoreTenant, ignoreDeletable);

        final List<Selection<?>> selections = new ArrayList<>();
        if (selector != null) {

            List<Selection<?>> rootSelection = selector.select(root, criteriaBuilder);
            if (rootSelection != null && !rootSelection.isEmpty()) {
                selections.addAll(rootSelection);
            }
        }

        if (CollectionUtils.isNotEmpty(selections)) {

            selections.forEach(x -> {
                if (StringUtils.isBlank(x.getAlias()) && x instanceof PathImplementor) {
                    x.alias(
                            ((PathImplementor<?>) x).getAttribute().getName()
                    );
                }
            });

            criteriaQuery = criteriaQuery.multiselect(selections);
        } else if (isTuple || isMap) {
            criteriaQuery = criteriaQuery.multiselect(ManagedTypeUtils.selections(root));
        }

        if (groupBy != null) {
            criteriaQuery = criteriaQuery.groupBy(groupBy.grouping(root, criteriaBuilder));
        }

        if (having != null) {
            criteriaQuery = criteriaQuery.having(having.toPredicate(root, criteriaBuilder));
        }

        if (sort != null) {
            criteriaQuery = criteriaQuery.orderBy(sort.sort(root, criteriaBuilder));
        }

        return criteriaQuery;
    }


    protected <R> List<R> doFind(FindOptions<T> findOptions, Class<R> resultClass) {

        return doFind(findOptions.filter(), findOptions.select(), findOptions.groupBy(), findOptions.having()
                , findOptions.join(), findOptions.sort(), findOptions.skip(), findOptions.limit(), resultClass);

    }

    protected <R> List<R> doFind(
            Filter<T> filter
            , Selector<T> selector
            , GroupBy<T> groupBy
            , Filter<T> having
            , Joiner<T> joiner
            , Sorter<T> sort
            , int skip, int limit
            , Class<R> resultClass) {

        TypedQuery<R> query = entityManager.createQuery(buildQuery(filter, selector, groupBy, having, joiner, sort, resultClass));
        if (skip > MAX_SKIP) {
            throw new RuntimeException(String.format("this start position (%s) is greater than MAX_SKIP (%s)"
                    , skip, MAX_SKIP));
        } else if (skip > 0) {
            query.setFirstResult(skip);
        }

        if (limit > 0) {
            query.setMaxResults(limit);
        } else {
            query.setMaxResults(MAX_LIMIT);
        }
        disableCache(query);

        List<R> res;
        boolean isMap = Map.class.isAssignableFrom(resultClass);
        if (isMap) {
            res = query.getResultList().stream().map(x ->
                    (R) TupleUtils.tupleToMap((Tuple) x)
            ).collect(Collectors.toList());
        } else {
            res = query.getResultList();
        }

        entityManager.clear();
        return res;
    }

    protected long doCount(Filter<T> filter, GroupBy<T> groupBy) {

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        //paged
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> root = countQuery.from(getEntityType());

        boolean ignoreTenant = false;
        boolean ignoreDeletable = false;

        if (filter != null) {
            countQuery = countQuery.where(filter.toPredicate(root, criteriaBuilder));
            ignoreTenant = filter.ignoreTenant();
            ignoreDeletable = filter.ignoreDeletable();
        }

        if (groupBy != null) {

            List<Expression<?>> expressions = groupBy.grouping(root, criteriaBuilder);
            List<Selection<?>> count = SelectorBuilder.empty(root, criteriaBuilder)
                    .countDistinct(expressions, "")
                    .build();

            countQuery = countQuery.select((Expression<Long>) count.get(0));

        } else {
            countQuery = countQuery.select(criteriaBuilder.count(root));
        }

        employDefaultCriteria(criteriaBuilder, root, countQuery
                , ignoreTenant, ignoreDeletable);

        TypedQuery<Long> query = entityManager.createQuery(countQuery);
        disableCache(query);
        return query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    protected CriteriaBuilder.In<ID> idsCriteria(CriteriaBuilder criteriaBuilder, Root<T> root, Iterable<? extends ID> ids) {
        CriteriaBuilder.In<ID> in = (CriteriaBuilder.In) criteriaBuilder.in(root.get(entityInformation.getIdAttribute()));
        for (ID id : ids) {
            in.value(id);
        }

        return in;
    }

    protected void employDefaultCriteria(CriteriaBuilder criteriaBuilder, Root<T> root, CommonAbstractCriteria criteria
            , boolean ignoreTenant, boolean ignoreDeletable) {
        if (!ignoreDeletable) {
            employDeletable(criteriaBuilder, root, criteria);
        }
        if (!ignoreTenant) {
            employTenant(criteriaBuilder, root, criteria);
        }
    }

    protected void employDeletable(CriteriaBuilder criteriaBuilder, Root<T> root, CommonAbstractCriteria criteria) {

        if (Deletable.class.isAssignableFrom(getEntityType())) {

            Predicate delPredicate = criteriaBuilder.equal(root.get(entityInformation.deleteAttributeName()), false);
            Predicate[] predicates = null;

            if (criteria.getRestriction() != null) {
                if (!PredicateUtils.existsAttribute(criteria.getRestriction(), entityInformation.deleteAttributeName())) {
                    predicates = new Predicate[]{criteria.getRestriction(), delPredicate};
                } else {
                    predicates = new Predicate[]{criteria.getRestriction()};
                }
            } else {
                predicates = new Predicate[]{delPredicate};
            }

            if (criteria instanceof CriteriaQuery<?>) {
                ((CriteriaQuery<?>) criteria).where(predicates);
            } else if (criteria instanceof CriteriaUpdate<?>) {
                ((CriteriaUpdate<?>) criteria).where(predicates);
            }
        }

    }


    @Deprecated
    protected void employTenant(CriteriaBuilder criteriaBuilder, Root<T> root, CommonAbstractCriteria criteria) {

//        if (Tenant.class.isAssignableFrom(getEntityType())) {
//
//            Long tenantId = TenantUtils.getTenantId();
//            if (tenantId != null) {
//
//                Predicate tenantPredicate = criteriaBuilder.equal(root.get(entityInformation.tenantIdAttributeName()), tenantId);
//                Predicate[] predicates = null;
//
//                if (criteria.getRestriction() != null) {
//                    if (!PredicateUtils.existsAttribute(criteria.getRestriction(), entityInformation.tenantIdAttributeName())) {
//                        predicates = new Predicate[]{criteria.getRestriction(), tenantPredicate};
//                    } else {
//                        predicates = new Predicate[]{criteria.getRestriction()};
//                    }
//                } else {
//                    predicates = new Predicate[]{tenantPredicate};
//                }
//
//                if (criteria instanceof CriteriaQuery<?>) {
//                    ((CriteriaQuery<?>) criteria).where(predicates);
//                } else if (criteria instanceof CriteriaUpdate<?>) {
//                    ((CriteriaUpdate<?>) criteria).where(predicates);
//                }
//            }
//
//        }

    }

    protected String getDeleteAllQueryString() {
        return getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName());
    }

    protected String getCountQueryString() {

        String countQuery = String.format(COUNT_QUERY_STRING, provider.getCountQueryPlaceholder(), "%s");
        return getQueryString(countQuery, entityInformation.getEntityName());
    }

    protected void callGlobalInterceptors(final Class<? extends Annotation> event,
                                          Object entity,
                                          CriteriaUpdate<?> criteriaUpdate,
                                          CriteriaBuilder criteriaBuilder) {

        for (final EntityInterceptor ei : entityInformation.getInterceptors()) {
            if (log.isDebugEnabled()) {
                log.info("Calling interceptor method {} on {}", event.getSimpleName(), ei);
            }

            if (PreUpdate.class.equals(event)) {
                ei.preUpdate(criteriaUpdate, criteriaBuilder, entityInformation);
            }

            if (PrePersist.class.equals(event)) {
                ei.prePersist(entity);
            }
        }
    }

    /**
     * Configures a custom {@link CrudMethodMetadata} to be used to detect {@link LockModeType}s and query hints to be
     * applied to queries.
     *
     * @param crudMethodMetadata {@link CrudMethodMetadata}
     */
    @Override
    public void setRepositoryMethodMetadata(CrudMethodMetadata crudMethodMetadata) {
        this.metadata = crudMethodMetadata;
    }

    @Override
    public boolean containsAttribute(String attributeName) {
        return entityInformation.containsAttribute(attributeName);
    }
}
