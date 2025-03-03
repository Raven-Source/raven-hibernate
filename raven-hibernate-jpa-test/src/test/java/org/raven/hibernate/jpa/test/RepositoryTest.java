package org.raven.hibernate.jpa.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.raven.commons.context.Context;
import org.raven.commons.context.ContextHolder;
import org.raven.commons.util.DateTimeUtils;
import org.raven.commons.util.Lists;
import org.raven.hibernate.data.PageableWrapper;
import org.raven.hibernate.jpa.*;
import org.raven.hibernate.jpa.test.repository.*;
import org.raven.hibernate.predicate.ArrayHasType;
import org.raven.hibernate.predicate.ArrayValueType;
import org.raven.hibernate.util.TupleUtils;
import org.raven.serializer.withJackson.JsonUtils;
import org.raven.spring.commons.util.ContextHolderProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static javax.persistence.criteria.Predicate.BooleanOperator.AND;
import static javax.persistence.criteria.Predicate.BooleanOperator.OR;

import org.raven.hibernate.jpa.test.repository.Orders.Fields;

@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
@Slf4j
public class RepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    private final int seed = 50;

    @Before
    @Transactional
    public void init() throws Exception {
        orderRepository.deleteAll();
        userRepository.deleteAll();
        itemsRepository.deleteAll();

        List<Long> uids = new ArrayList<>();
        List<User> users = new ArrayList<>();

        for (int i = 0; i < seed; i++) {

            User user = new User();
            user.setName("用户：" + i);
            user.setTenantId(((long) i % 2) + 1);

            if (i % 2 == 0) {
                user.setTime(LocalTime.of(23, 59, 59, 999_999_999));
            } else {
                user.setTime(LocalTime.MIN);
            }

            if (i % 4 == 0) {
                user.setType(UserType.Development);
            } else {
                user.setType(UserType.Operation);
            }

            users.add(user);
        }

        userRepository.insertBatch(users);
        userRepository.flush();

        uids = users.stream().map(User::getId).collect(Collectors.toList());

        List<Items> items = new ArrayList<>();

        for (int i = 0; i < (seed / 5); i++) {
            Items item = new Items();
            item.setName("商品：" + i);

            items.add(item);
        }
        itemsRepository.insertBatch(items);


        List<Orders> list = new ArrayList<>();
        Orders orders = null;

        for (int i = 0; i < seed * 2; i++) {
            orders = new Orders();

//            User user = new User();
//            user.setId(((long) i / 2) + 1);
//            user.setTenantId(((long) i % 2) + 1);

//            orders.setUser(user);
//            orders.setId(i + 1L);
            orders.setUid((long) uids.get(i / 2));
            orders.setItemsId(
                    items.get(i % items.size()).getId()
            );
            orders.setName("订单" + i);
            if (i % 2 == 0) {
                orders.setDeleted(true);
            }

            if (i % 3 == 0) {
                orders.setIsPay(true);
            }

            if (i % 5 == 0) {
                orders.setStatus(StatusType.Fail);
            }

            orders.setPrice(BigDecimal.valueOf(Math.random() * 100D));

            orders.setCreateTime(DateTimeUtils.parse("20230102", DateTimeUtils.DATE_DAY_FORMAT));

            orders.getRefs().addAll(Arrays.asList(i, i + 1, i + 2, i + 3, i + 4));

            Box box = new Box();
            box.setId(i * 2L);
            box.setName("盒子" + box.getId());
            box.setIsOpen(true);
            orders.setBox(box);

            list.add(orders);

        }

        StopWatch stopWatch = new StopWatch("jpa batch");
        stopWatch.start();

        orderRepository.insertBatch(list);
        orderRepository.flush();
        stopWatch.stop();
        System.out.println("totalTime:" + stopWatch.getTotalTimeMillis());
    }

    @After
    public void clear() {
//        repository.deleteAll();
    }

    @Test
    public void getTest() {

        List<Orders> list = orderRepository.findAll();
        System.out.println(list.size());
        Assert.assertEquals(list.size(), seed);

        // enum
        List<Orders> ordersList = orderRepository.getByStatus(Status.Finish);
        Assert.assertTrue(
                orderRepository.containsAttribute(Fields.deleted) &&
                        orderRepository.containsAttribute("deleted")
        );

        //TODO Native Enum
        ordersList = orderRepository.getByStatusNative(Status.Finish.getValue());

        Orders orders;
        orders = list.stream().findFirst().get();
        orders = orderRepository.findById(orders.getId()).get();

        System.out.println(orders);

        List<Long> ids = list.stream().map(Orders::getId).collect(Collectors.toList());

        list = orderRepository.findAllById(ids);

        Assert.assertEquals(list.size(), ids.size());
        System.out.println(list.size());

        System.out.println(orders);

        list = orderRepository.findList(f -> f
                .isNull(Fields.name)
                .isNotNull(Fields.status)
                .build()
        );
        System.out.println(list);

        ids = Lists.newArrayList();
        list = orderRepository.list(ids);
        System.out.println(list);

        ids = null;
        list = orderRepository.list(ids);
        System.out.println(list);

        orders = orderRepository.findOne(f -> f
                .equal(Fields.status, StatusType.Finish)
                .in(Fields.uid, Lists.newArrayList(2, 3))
                .gt(Fields.price, 198)
                .notEqual(Fields.isPay, false)
                .likeLeft(Fields.name, "iphone")
                .build()
        );

        orders = orderRepository.findOne(123L);

        List<Long> uids = Lists.newArrayList();

        orders = orderRepository.findOne(f ->
                f.condition(uids != null && !uids.isEmpty(), x -> x.in(Fields.uid, uids))
                        .build()
        );


        orders = orderRepository.findOne(f -> {

                    Predicate x = f.newFilterBuilder()
                            .gt(Fields.price, 198)
                            .equal(Fields.status, StatusType.Finish)
                            .build(OR);

                    Predicate y = f.newFilterBuilder()
                            .lt(Fields.price, 58)
                            .equal(Fields.status, StatusType.Fail)
                            .build(AND);

                    Predicate z = f.newFilterBuilder()
                            .ge(Fields.price, 999)
                            .equal(Fields.isPay, true)
                            .build(OR);


                    return f.add(x)
                            .add(y)
                            .add(z)
                            .build();
                }
        );

    }

    @Test
    public void caseWhenTest() {

        List<Map> ordersList;

        FindOptions<Orders> findOptions = FindOptions.<Orders>empty()
                .filter(f -> f
                        .equal(Fields.status, StatusType.Finish)
                        .build())
                .select(s -> s
                        .column(Fields.price)
                        .caseWhen(c -> c
                                        .when(x -> x
                                                .lt(Fields.price, 50)
                                                .build(), "cheap")
                                        .when(x -> x
                                                .ge(Fields.price, 50)
                                                .lt(Fields.price, 60)
                                                .build(), "general")
                                        .otherwise("expensive")
                                , "priceRange")
                        .build()
                );

        ordersList = orderRepository.findList(findOptions, Map.class);

        System.out.println("caseTest orders size: " + ordersList.size());
    }

    @Test
    public void findPage() {

        Pageable pageable = PageRequest.of(2, 10);
        Page<Orders> ordersPage = orderRepository.findPage(
                f -> f
                        .equal(Fields.status, StatusType.Finish)
                        .build(),
                pageable
        );

        Assert.assertEquals(ordersPage.getContent().size(), pageable.getPageSize());
        Assert.assertEquals(ordersPage.getSize(), pageable.getPageSize());
        Assert.assertEquals(ordersPage.getTotalElements(), 40);
        Assert.assertEquals(ordersPage.getNumber(), pageable.getPageNumber());
    }

    @Test
    public void byDBColumnName() {

        // getAttributeName by propertyColumnName
        long count = orderRepository.count(filter -> {
            return filter.equal("is_pay", true)
                    .lessThanOrEqualTo("create_time", new Date())
                    .build();
        });
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getMapTest() {

        List<Tuple> list = orderRepository.findList(
                (Filter<Orders>) null
//                , null
                , (root, builder) -> {

                    return Lists.newArrayList(
                            root.get(Fields.name),
                            root.get(Fields.uid),
                            root.get(Fields.isPay)

                    );

                }
                , null, Tuple.class);
        System.out.println(list.size());

        list = orderRepository.findList(
                (Filter<Orders>) null
//                , null
                , (root, builder) -> {

                    return Lists.newArrayList(
                            root.get(Fields.name),
                            root.get(Fields.uid),
                            root.get(Fields.isPay),
                            root.get(Fields.box)

                    );

                }
                , null, Tuple.class);
        System.out.println(list.size());

        for (Tuple tuple : list) {
            Object uid = tuple.get("uid");
            Object name = tuple.get("name");
            Object isPay = tuple.get("isPay");

            System.out.println(String.format("uid:%s,name:%s,isPay:%s", uid, name, isPay));
        }

        list = orderRepository.findList(
                (Filter<Orders>) null
                , null
                , null, Tuple.class);

        System.out.println(list.size());

        for (Tuple tuple : list) {
            Object uid = tuple.get("uid");
            Object name = tuple.get("name");
            Object isPay = tuple.get("isPay");

            System.out.println(String.format("uid:%s,name:%s,isPay:%s", uid, name, isPay));
        }

        list = orderRepository.findList(
                (Filter<Orders>) null
                , (root, builder) -> {

                    return Lists.newArrayList(
                            root.get(Fields.name).alias("n"),
                            root.get(Fields.uid).alias("u"),
                            root.get(Fields.isPay).alias("ip")

                    );

                }
                , null, Tuple.class);

        System.out.println(list.size());

        for (Tuple tuple : list) {
            Object uid = tuple.get("u");
            Object name = tuple.get("n");
            Object isPay = tuple.get("ip");

            System.out.println(String.format("uid:%s,name:%s,isPay:%s", uid, name, isPay));
        }


        List<Map> maps = orderRepository.findList(
                (Filter<Orders>) null
                , (root, builder) -> {

                    return Lists.newArrayList(
                            root.get(Fields.name).alias("n"),
                            root.get(Fields.uid).alias("u"),
                            root.get(Fields.isPay).alias("ip")

                    );

                }
                , null, Map.class);

        System.out.println(maps.size());
        for (Map map : maps) {
            Object uid = map.get("u");
            Object name = map.get("n");
            Object isPay = map.get("ip");

            System.out.println(String.format("uid:%s,name:%s,isPay:%s", uid, name, isPay));
        }

        maps = orderRepository.findList(
                (Filter<Orders>) null
                , null
                , null, Map.class);

        System.out.println(maps.size());
        for (Map map : maps) {
            Object uid = map.get("uid");
            Object name = map.get("name");
            Object isPay = map.get("isPay");

            System.out.println(String.format("uid:%s,name:%s,isPay:%s", uid, name, isPay));
        }
    }

    @Test
    public void selectTest() {

        List<Orders> ordersList = orderRepository.findList(
                f -> f.equal(Fields.status, StatusType.Finish).build(),
                s -> s.select(Fields.id, Fields.deleted).build()
        );

        System.out.println(ordersList);

        //通过 Filter、Selector，构建 FilterBuilder、SelectorBuilder
        SimpleOrders simpleOrders = orderRepository.findOne(
                // Filter<T>
                (root, builder) ->
                        new FilterBuilder<>(root, builder)
                                .equal(Fields.deleted, true)
                                .build(),
                // Selector<T>
                (root, builder) ->
                        new SelectorBuilder<>(root, builder)
                                .select(Fields.id, Fields.deleted)
                                .build(),
                null,
                SimpleOrders.class);

        System.out.println(simpleOrders);
        Assert.assertNotNull(simpleOrders);

        //自动构建 FilterBuilder、SelectorBuilder
        simpleOrders = orderRepository.findOne(
                // Function<FilterBuilder<T>, Expression<Boolean>>
                f -> f.equal(Fields.deleted, true)
                        .build(),
                // Function<SelectorBuilder<T>, List<Selection<?>>>
                s -> s.select(Fields.id, Fields.status)
                        .build(),
                null,
                SimpleOrders.class
        );

        System.out.println(simpleOrders);
        Assert.assertNotNull(simpleOrders);

    }

    public void ignoreTenantTest() {

        ContextHolder contextHolder = ContextHolderProvider.getContextHolder();
        Context context = contextHolder.getContext();
//        context.setAttribute(CommonConstant.TENANT_ID, 1);

        List<User> users = userRepository.findList((Filter<User>) null);
        int total = users.size();

        System.out.println("list total:" + total);
        Assert.assertEquals(total, seed / 2);
        users = userRepository.findList(new Filter<User>() {

            @Override
            public boolean ignoreTenant() {
                return true;
            }

            @Override
            public Expression<Boolean> toPredicate(Root<User> root, CriteriaBuilder builder) {
                return builder.and();
            }
        });

        total = users.size();

        System.out.println("list total:" + total);
        Assert.assertEquals(total, seed);

        users = userRepository.findList(
                Filter.of((root, builder) -> builder.and(), true)
        );

        total = users.size();

        System.out.println("list total:" + total);
        Assert.assertEquals(total, seed);

    }

    @Test
    public void inTest() {

        List<Orders> list = orderRepository.findAll();
        int total = list.size();

        System.out.println("list total:" + list.size());

        List<Long> ids = list.stream()
                .filter(x -> x.getIsPay())
                .map(x -> x.getId())
                .collect(Collectors.toList());

        list = orderRepository.findList(f -> f.in(Fields.id, ids).build());
        System.out.println("list IsPay:" + list.size());
        Assert.assertEquals(list.size(), ids.size());

        list = orderRepository.findList(f -> f.notIn(Fields.id, ids).build());
        System.out.println("list Not IsPay:" + list.size());
        Assert.assertEquals(list.size(), total - ids.size());

    }

    @Test
    public void orderByTest() {

        List<Orders> list = orderRepository.findAll();
        // sort
        long maxId = list.stream().map(Orders::getId).max(Long::compareTo).get();
        long minId = list.stream().map(Orders::getId).min(Long::compareTo).get();
        System.out.println("maxId:" + maxId);
        System.out.println("minId:" + minId);

        Sort sort = Sort.by(Sort.Direction.DESC, Fields.id);
        List<Orders> sortList = orderRepository.findList(null, null, o ->
                o.by(sort.toList()).build()
        );

        sortList = orderRepository.findList(null, null, o ->
                o.desc(Fields.id).build()
        );


        Assert.assertEquals((long) sortList.get(0).getId(), maxId);
        Assert.assertEquals((long) sortList.get(sortList.size() - 1).getId(), minId);

        FindOptions<Orders> findOptions = FindOptions.<Orders>empty()
                .sort(sort.toList());

        sortList = orderRepository.findList(findOptions);
        Assert.assertEquals((long) sortList.get(0).getId(), maxId);
        Assert.assertEquals((long) sortList.get(sortList.size() - 1).getId(), minId);
    }


    @Test
    public void groupBy2Test() {

        List<?> list = orderRepository.group();

        System.out.println("list:" + list);
    }


    @Test
    public void groupByTest() {

        List<Orders> list = orderRepository.findAll();
        // sort
        BigDecimal sum = list.stream().map(Orders::getPrice).reduce(BigDecimal::add).get();
        System.out.println("sum:" + sum);


        SingleExpression<Orders, Number> quotExt = (r, b) -> b.quot(
                b.sum(r.get(Fields.price)),
                b.count(r.get(Fields.price))
        );

        SingleExpression<Orders, Number> orderExt = (r, b) -> {

            Expression<Number> exp = b.sum(r.get(Fields.price));
            exp.alias("sumPrice");
            return exp;
        };


        FindOptions<Orders> findOptions = FindOptions.<Orders>empty()
                .select(s -> s
                                .column(Fields.isPay)
                                .sum(Fields.price, OrdersSum.Fields.total)
                                .min(Fields.price, OrdersSum.Fields.min)
                                .max(Fields.price, OrdersSum.Fields.max)
                                .count(Fields.price, OrdersSum.Fields.count)
                                .exp(quotExt, "quot")
//                        .exp((r, b) -> {
//                                    return b.countDistinct(
//                                            b.function("concat", String.class,
//                                                    r.get(Orders.Fields.isPay),
//                                                    b.literal("_"),
//                                                    r.get(Fields.status)
//                                            )
//                                    );
//                                }
//                                , "quot2")
                                .build()
                )
                .groupBy(x -> x.groupBy(Fields.isPay, Fields.status).build());


        List<Tuple> orders = orderRepository.findList(findOptions, Tuple.class);
        System.out.print("orders:");
        for (Tuple order : orders) {

            System.out.print("isPay:" + order.get(Fields.isPay));
            System.out.print(",total:" + order.get(OrdersSum.Fields.total));
        }
        System.out.println();

        List<OrdersSum> orders2 = orderRepository.findList(findOptions, OrdersSum.class);
        System.out.println("orders2:" + JsonUtils.toJsonString(orders2));

        findOptions.limit(10);
        Page<OrdersSum> page = orderRepository.findPage(findOptions, OrdersSum.class);
        System.out.println("total:" + page.getTotalElements());

        findOptions.having(f -> f.gt(quotExt, 1).build());
        findOptions.sort(s -> s.desc(orderExt).build());
//        findOptions.having((root, builder) -> builder.greaterThan(builder.literal("quot"), 1));
//        findOptions.having(f -> f.gt("quot", 1).build());
        orders = orderRepository.findList(findOptions, Tuple.class);
        System.out.println("orders:");
        for (Tuple order : orders) {
            System.out.print(TupleUtils.tupleToMap(order));
        }
        System.out.println();

        CountOptions<Orders> countOptions = CountOptions.<Orders>empty()
                .groupBy(x -> x.groupBy(Fields.isPay, Fields.status).build());
        long count = orderRepository.count(countOptions);
        System.out.println("count:" + count);

    }

    @Test
    public void joinTest() {

        List<Orders> orders = null;
        List<Map> maps = null;

        maps = orderRepository.leftJoinUser(true);

        // fetch
        FindOptions<Orders> findOptions = FindOptions.<Orders>empty()
                .join(x -> x
                        .<User>fetch(Fields.user)
                        .build()
                );

        orders = orderRepository.findList(findOptions);
        List<User> users = orders.stream().map(Orders::getUser).collect(Collectors.toList());

        Assert.assertEquals(users.size(), orders.size());
        for (User user : users) {
            Assert.assertNotNull(user.getId());
            Assert.assertNotNull(user.getName());
        }

        // join
        findOptions = FindOptions.<Orders>empty()
                .join(x -> x
                        .join(Fields.user)
                        .build()
                );

        orders = orderRepository.findList(findOptions);

        try {
            User user = orders.get(0).getUser();
            String userName = user.getName();
        } catch (Exception e) {
            Assert.assertEquals(LazyInitializationException.class, e.getClass());
        }

        findOptions = FindOptions.<Orders>empty()
                .join(x -> x
                        .join(Fields.user, JoinType.LEFT)
                        .build()
                )
                .select(s -> s
                        .allColumn(false)
                        .column(Fields.user)
                        .build()
                )
        ;

        maps = orderRepository.findList(findOptions, Map.class);

//        MutableObject<Join<Orders, User>> userJoin = new MutableObject<>();

        findOptions = FindOptions.<Orders>empty()
                .join(x -> x
//                        .join(Fields.user, JoinType.LEFT, j -> {
//                            j.on(
//                                    x.getBuilder().equal(x.getAttribute("uid"), j.get("id"))
//                            );
//                        })
                                .join(Fields.user, j -> j
                                        .equal(User.Fields.type, UserType.Development)
                                        .build()
                                )
                                .build()
                )
                .select(s -> s
                                .column(Fields.id)
                                .column(Fields.name, OrdersJoinUser.Fields.orderName)
//                        .column(s.getJoinAttribute(Fields.user, User.Fields.name), OrdersJoinUser.Fields.userName)
                                .<User>withJoin(Fields.user,
                                        js -> js
                                                .column(User.Fields.name, OrdersJoinUser.Fields.userName)
                                                .column(User.Fields.time, OrdersJoinUser.Fields.time)
                                                .build()
                                )
                                .build()
                ).sort(
                        o -> o
                                .<User>withJoin(Fields.user,
                                        jo -> jo
                                                .desc(User.Fields.time)
                                                .build())
                                .build()
                )
        ;

        List<OrdersJoinUser> joinUsers = orderRepository.findList(findOptions, OrdersJoinUser.class);

        Assert.assertEquals(users.stream().filter(x -> x.getType().equals(UserType.Development)).count(), joinUsers.size());

    }

    @Setter
    @Getter
    @FieldNameConstants
    @AllArgsConstructor
    public static class OrdersJoinUser {

        private Long id;
        private String orderName;
        private String userName;
        private LocalTime time;
    }

    @Setter
    @Getter
    @FieldNameConstants
    @AllArgsConstructor
    public static class OrdersSum {
        private Boolean isPay;
        private BigDecimal total;
        private BigDecimal min;
        private BigDecimal max;
        private long count;
        private BigDecimal quot;
    }

    @Test
    public void pageTest() {

        Page<Orders> ordersPage = orderRepository.page(false,
                PageableWrapper.wrapper(
                        org.raven.commons.contract.PageRequest.of(0, 10),
                        Sort.by(Sort.Direction.DESC, Fields.id)
                )
        );

        System.out.println(ordersPage);
    }

    @Test
    public void arrayHas() {

        List<Orders> ordersList = null;

        ordersList = orderRepository.findList(Filter.of(f ->
                        f.arrayHas(Fields.refs
                                , ArrayHasType.ANY
                                , ArrayValueType.INT32_ARRAY
                                , Lists.newArrayList(5, 6)
                        ).build()
                , true, true)
        );

        System.out.println(ordersList.size());
        System.out.println(ordersList);

        Assert.assertTrue(ordersList.size() == 6);

        ordersList = orderRepository.findList(Filter.of(f ->
                        f.arrayHas(Fields.refs
                                , ArrayHasType.ALL
                                , ArrayValueType.INT32_ARRAY
                                , Lists.newArrayList("6", "5")
                        ).build()
                , true, true)
        );

        System.out.println(ordersList.size());
        System.out.println(ordersList);

        Assert.assertTrue(ordersList.size() == 4);
    }

//    @Test
//    public void memberOfTest() {
//
//        List<Orders> ordersList = null;
//
//        ordersList = orderRepository.findList((r, builder) -> {
//
//            return
//                    builder.isTrue(
//                            builder.function(
//                                    "JSON_CONTAINS",
//                                    Boolean.class,
//                                    builder.function(
//                                            "JSON_EXTRACT",
//                                            List.class,
//                                            r.get(Orders.Fields.refs),
//                                            builder.literal("$[*]")
//                                    ),

    /// /                            builder.literal("refs->'$[*]'"),
//                                    builder.literal("[3]")
//
//                            )
//                    );
//        });
//
//        System.out.println(ordersList.size());
//        System.out.println(ordersList);
//
//
//    }
    @Test
    public void updateTest() {

        List<Orders> list = orderRepository.findAll();
        Orders orders = list.get(0);

        int res = orderRepository.update(
                (root, builder) ->
                        new FilterBuilder<>(root, builder)
                                .equal(Fields.id, orders.getId()).build(),
                (update, root, builder) ->
                        new UpdateSetBuilder<>(update, root, builder)
                                .set(Fields.name, 123)
                                .set(Fields.createTime, new Date())
        );


        Assert.assertEquals(res, 1);

        List<Integer> refs = Lists.newArrayList(888, 999);
        Date now = new Date();

        //自动构建 UpdateSetBuilder、FilterBuilder
        res = orderRepository.update(
                // Function<FilterBuilder<T>, Expression<Boolean>>
                f -> f.equal(Fields.id, orders.getId()).build(),
                // UpdateSetExpression<T> updateSetExpression
                u -> u.set(Fields.name, 123)
                        .set(Fields.status, StatusType.Fail)
                        .set(Fields.isPay, !orders.getIsPay())
                        .set(Fields.createTime, now)
                        .sum(Fields.version, 1L)
                        .condition(!refs.isEmpty(), x -> x.set(Fields.refs, refs))
        );

        Orders upOrders = orderRepository.findOne(orders.getId());
        Assert.assertEquals(upOrders.getName(), "123");
        Assert.assertEquals(upOrders.getStatus(), StatusType.Fail);
        Assert.assertEquals(upOrders.getVersion().longValue(), orders.getVersion() + 1L);
        Assert.assertEquals(upOrders.getCreateTime().getTime(), now.getTime());

        Assert.assertEquals(res, 1);

        System.out.println(res);

        orders.setRefs(Lists.newArrayList(999, 888));
        orderRepository.save(orders);

        res = orderRepository.update(
                // Function<FilterBuilder<T>, Expression<Boolean>>
                f -> f.equal(Fields.id, orders.getId()).build(),
                // UpdateSetExpression<T> updateSetExpression
                u -> u.set(Fields.refs, "[999,888]")
        );

        long value = orders.getVersion();
        long inc = 3L;
        res = orderRepository.update(
                // Function<FilterBuilder<T>, Expression<Boolean>>
                f -> f.equal(Fields.id, orders.getId()).build(),
                // UpdateSetExpression<T> updateSetExpression
                u -> u.sum(Fields.version, inc)
        );

        long value2 = orderRepository.findOne(orders.getId()).getVersion();
        Assert.assertEquals(value + inc, value2);

        res = orderRepository.update(
                // Function<FilterBuilder<T>, Expression<Boolean>>
                f -> f.equal(Fields.id, orders.getId()).build(),
                // UpdateSetExpression<T> updateSetExpression
                u -> u.sum(Fields.version, -inc)
        );

        long value3 = orderRepository.findOne(orders.getId()).getVersion();
        Assert.assertEquals(value, value3);

        res = orderRepository.update(
                // Function<FilterBuilder<T>, Expression<Boolean>>
                f -> f.equal(Fields.id, orders.getId()).build(),
                // UpdateSetExpression<T> updateSetExpression
                u -> u.diff(Fields.version, 13)
        );

        value3 = orderRepository.findOne(orders.getId()).getVersion();
        Assert.assertEquals(value - 13, value3);

    }

    @Test
    public void saveTest() {

        List<Orders> list = orderRepository.findAll();
        Orders orders = list.get(0);

        orders = orderRepository.findOne(orders.getId());

        orders.setName("new name");

        orderRepository.saveAndFlush(orders);

    }

    @Test
    public void insertTest() {

        long startTime = System.currentTimeMillis();
        int seed = 100;
        System.out.println("startTime: " + startTime);

        List<Orders> list = new ArrayList<>(1000);
        Orders orders;
        for (int i = 0; i < seed; i++) {

            orders = new Orders();
//            orders.setId(i * 100L);
            orders.setName("new name");

            list.add(orders);

            if (list.size() == 1000) {

                orderRepository.insertBatch(list);
                list.clear();
            }
        }

        if (!list.isEmpty()) {

            orderRepository.insertBatch(list);
            list.clear();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("insert " + seed + " orders time:" + (endTime - startTime));

    }


//    @Test
//    public void insertBigBatchTest() {
//
//        long startTime = System.currentTimeMillis();
//        int seed = 100000;
//        System.out.println("startTime: " + startTime);
//
//        List<Orders> list = new ArrayList<>(1000);
//        Orders orders;
//        for (int i = 0; i < seed; i++) {
//
//            orders = new Orders();
//            orders.setName("new name");
//
//            list.add(orders);
//
//            if (list.size() == 1000) {
//
//                orderRepository.insertBigBatch(list);
//                list.clear();
//            }
//        }
//
//        if (!list.isEmpty()) {
//
//            orderRepository.insertBigBatch(list);
//            list.clear();
//        }
//
//        long endTime = System.currentTimeMillis();
//        System.out.println("insert " + seed + " orders time:" + (endTime - startTime));
//
//    }

    @Test
    public void insertTest2() {

        long startTime = System.currentTimeMillis();
        int seed = 100;
        System.out.println("startTime: " + startTime);

        List<Orders> list = new ArrayList<>(1000);
        Orders orders;
        for (int i = 0; i < seed; i++) {

            orders = new Orders();
//            orders.setId(i * 100L);
            orders.setName("insertTest2");

            list.add(orders);

            if (list.size() == 100) {

                orderRepository.insertAll(list);
                list.clear();
            }
        }

        if (!list.isEmpty()) {

            orderRepository.insertAll(list);
            list.clear();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("insert " + seed + " orders time:" + (endTime - startTime));

    }

    @Test
    public void countTest() {

        long count = orderRepository.count(f -> f.equal(Fields.deleted, true).build());
        System.out.println(count);
        Assert.assertEquals(count, seed);


//        count = orderRepository.countDistinct(
//                f -> f.equal(Orders.Fields.status, 3).build(),
//                (root, builder) -> builder.concat(builder.concat(root.get(Orders.Fields.isPay), root.get(Orders.Fields.status)), "")
//        );
//
//        System.out.println(count);
//        Assert.assertEquals(count, 2);
//
//        count = orderRepository.countDistinct(
//                f -> f.equal(Orders.Fields.status, 3).build(),
//                (root, builder) -> root.get(Orders.Fields.isPay)
//        );

//        System.out.println(count);
//        Assert.assertEquals(count, 2);

    }


    @Test
    public void existsTest() {

        boolean exists = orderRepository.exists(f -> f.equal(Fields.deleted, true).build());
        System.out.println("exists: " + exists);

    }


    @Test
    public void deletableTest() {

        Orders orders = orderRepository.findOne((root, builder) -> {
            return new FilterBuilder<>(root, builder).equal(Fields.deleted, true).build();

        });

        Assert.assertEquals(orders.getDeleted(), true);

        orders = orderRepository.findOne((Filter) null);

        Assert.assertEquals(orders.getDeleted(), false);

        orders = orderRepository.findOne((root, builder) -> {
            return new FilterBuilder<>(root, builder).equal(Fields.deleted, false).build();
        });

        Assert.assertEquals(orders.getDeleted(), false);

        orders = orderRepository.findOne(f ->
                f.equal(Fields.deleted, false).build()
        );

        Assert.assertEquals(orders.getDeleted(), false);

    }

    @Test
    public void incrTest() throws Exception {
        int seed = 100;

        List<CompletableFuture<?>> failures = new ArrayList<>(seed);

        long start = System.currentTimeMillis();

        for (int i = 0; i < seed; i++) {

            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() ->
                    orderRepository.incrVersion(1L)
            );

            failures.add(future);

        }

        CompletableFuture.allOf(failures.toArray(new CompletableFuture[0])).get();

        long end = System.currentTimeMillis();

        System.out.println("qps:" + seed / (double) (end - start) * 1000.0);

        System.out.println("end");

    }

    public void findBuilderTest() {
        FindOptions.empty()
                .filter(f -> f.equal(Fields.deleted, true).build())
                .limit(5)
                .skip(2);


    }

//    @Test
//    public void subqueryTest() {
//
//        repository.update(u -> {
//
//
//        }, f -> f.equal("status", 0).build());
//
//
//        Subquery<Long> productInventorySubquery = orderItemQuery.subquery(Integer.class);
//    }

}
