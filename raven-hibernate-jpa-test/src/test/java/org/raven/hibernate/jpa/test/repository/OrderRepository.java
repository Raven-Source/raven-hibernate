package org.raven.hibernate.jpa.test.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.raven.commons.util.Lists;
import org.raven.hibernate.jpa.JpaRepositorySupport;
import org.raven.hibernate.util.TupleUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface OrderRepository extends JpaRepositorySupport<Orders, Long> {

    @Override
    @Transactional
    @Modifying
    @Query(value = " truncate table t_orders ", nativeQuery = true)
    void deleteAll();

    @Transactional
    @Modifying
    @Query(" update Orders set version = version + 1 where id=:id ")
    int incrVersion(@Param("id") Long id);

    @Query("from Orders where (:ids is null or id in :ids)")
    List<Orders> list(@Param("ids") List<Long> ids);


    @Query("from Orders t where t.deleted = :deleted")
    Page<Orders> page(@Param("deleted") Boolean deleted,
                      Pageable page);

    @Query(value = " select * from t_orders t where t.status = :status", nativeQuery = true)
    List<Orders> getByStatusNative(@Param("status") int status);

    @Query(value = " select t from Orders t where t.status = :status")
    List<Orders> getByStatus(@Param("status") Status status);

    default List<Map> subquery() {

        EntityManager entityManager = entityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> criteriaQuery = builder.createTupleQuery();

        return null;
    }

    default List<Map> leftJoinUser(Boolean isPay) {

//        Metamodel metamodel = entityManager().getEntityManagerFactory().getMetamodel();
//        SingularAttribute<Orders, User> singularAttribute = metamodel.entity(Orders.class).getDeclaredSingularAttribute(User.Fields.name, User.class);

        EntityManager entityManager = entityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> criteriaQuery = builder.createTupleQuery();

        Root<Orders> ordersRoot = criteriaQuery.from(Orders.class);
        Root<Items> itemsRoot = criteriaQuery.from(Items.class);
//
//        SingularAttribute attribute = ordersRoot.getModel().getSingularAttribute(Orders.Fields.itemsId);
//
//        final Class<?> attributeType = attribute.getBindableJavaType();
//        SingularAttributeJoin singularAttributeJoin = new SingularAttributeJoin(
//                (CriteriaBuilderImpl) builder,
//                attributeType,
//                (PathSource) ordersRoot,
//                attribute,
//                JoinType.INNER
//        );

        Predicate joinCondition = builder.equal(ordersRoot.get(Orders.Fields.itemsId), itemsRoot.get(Items.Fields.id));

        Join<Orders, Items> itemsJoin = ordersRoot.join("items", JoinType.INNER);

//        itemsRoot.on(builder.equal(ordersRoot.get(Orders.Fields.itemsId), itemsRoot.get(Items.Fields.id)));

        List<Selection<?>> selections = Lists.newArrayList();
        selections.add(ordersRoot.get(Orders.Fields.name).alias("name"));
        selections.add(ordersRoot.get(Orders.Fields.itemsId).alias("itemsId"));
        selections.add(itemsRoot.get(Items.Fields.name).alias("itemsName"));

        criteriaQuery.where(joinCondition);
        criteriaQuery.multiselect(selections);

//        Session session = entityManager.unwrap(Session.class);
//        Criteria criteria = session.createCriteria(Orders.class, "o");
//        criteria.createAlias("o.items", "i", org.hibernate.sql.JoinType.INNER_JOIN);
//        criteria.add(Restrictions.eq("o.itemsId", "i.id"));

        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);

        List<Map> res;
        res = query.getResultList().stream().map(x ->
                TupleUtils.tupleToMap((Tuple) x)
        ).collect(Collectors.toList());

        entityManager.clear();

        return res;

//        return findList((root, builder) -> {
//
//            root.join("user", JoinType.LEFT);
//            root.join("itemsId", JoinType.LEFT);
//
////            joinRoot.on(builder.equal(ordersRoot.get("uid"), userRoot.get(User.Fields.id)));
//
//            return builder.equal(root.get(Orders.Fields.isPay), isPay);
//
//        });

    }

    @Data
    @FieldNameConstants
    @AllArgsConstructor
    public static class OrderGroup {

        private BigDecimal sumPrice;
    }

    default List<?> group() {

        CriteriaBuilder b = entityManager().getCriteriaBuilder();

        CriteriaQuery<?> criteriaQuery = b.createQuery(OrderGroup.class);
        Root<Orders> r = criteriaQuery.from(Orders.class);

        Expression<Number> exp = b.sum(r.get(Orders.Fields.price));
        Selection<Number> selection = exp.alias(OrderGroup.Fields.sumPrice);

        criteriaQuery.multiselect(selection);
        Predicate where = b.gt((Expression) exp, 12);
        criteriaQuery.having(where);
        criteriaQuery.groupBy(r.get(Orders.Fields.status));

        List<?> list = entityManager().createQuery(criteriaQuery).getResultList();
        return list;

    }

    @Transactional
    default int insertAll(List<Orders> orders) {

        EntityManager entityManager = entityManager();
        StringBuilder sql = new StringBuilder("insert into t_orders(id, uid, name, is_pay, price, deleted, status) ");
        boolean first = true;
        int i = 0;
        for (Orders order : orders) {
            if (first) {
                sql.append("values ");
                first = false;
            } else {
                sql.append(", ");
            }
            i++;
            sql.append("(?,?,?,?,?,?,?)");
        }

        javax.persistence.Query query = entityManager.createNativeQuery(sql.toString());

        i = 0;
        for (Orders order : orders) {
            int c = 1;
            query.setParameter((i * 7) + c++, order.getId())
                    .setParameter((i * 7) + c++, order.getUid())
                    .setParameter((i * 7) + c++, order.getName())
                    .setParameter((i * 7) + c++, order.getIsPay())
                    .setParameter((i * 7) + c++, order.getPrice())
                    .setParameter((i * 7) + c++, order.getDeleted())
                    .setParameter((i * 7) + c++, order.getStatus().getValue());
            i++;
        }

        return query.executeUpdate();

    }

}
