# 简介 | intro

针对 `spring-data-jpa` & `hibernate` 的一些写法调整和增强，目的简化写法以及避免一些问题。
使用的不是 `org.springframework.data.jpa.repository.support.SimpleJpaRepository` 实现，注意有一些差异。
实现的是标准JPA的 `CriteriaBuilder` 写法，而不是 `Spring` 的 `Example` 写法，`Example` 通过字段 `null` 方式实现条件逻辑容易出现当对象定义默认值时无法处理的情况。



## 快速开始 | quick start <a id="quick-start"></a>

### 1. 添加依赖：

```xml
<dependencies>
    <!-- hibernate start -->
    <dependency>
        <groupId>io.github.raven-source</groupId>
        <artifactId>raven-hibernate-jpa-starter</artifactId>
        <version>${raven-hibernate.version}</version>
    </dependency>

    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>${mysql-connector.version}</version>
    </dependency>
    <!-- hibernate end -->
</dependencies>
```

### 2. 完成配置后，开始查询

```java
// select id,name,status,... from t_orders 
// where status = 3
//  and uid in (2, 3)
//  and price > 198.0
//  and is_pay <> false
//  and name like '%iphone'
//  and name is null
//  and status is not null
Orders order = orderRepository.findOne(f -> 
      f.equal(Fields.status, StatusType.Finish)
        .in(Fields.uid, Lists.newArrayList(2, 3))
        .gt(Fields.price, 198)
        //.ge()\.lt()\.le()\greaterThan()\lessThan()...
        .notEqual(Fields.isPay, false)
        .likeLeft(Fields.name, "iphone")
        //.like()\.likeRight()
        .isNull(Fields.name)
        .isNotNull(Fields.status)
        .build()
);
```


## 目录

- **数据操作**
    - [配置准备](./docs/2.start.md)
    - [查询、删除操作](./docs/3.query&delete.md)
    - [保存、插入操作](./docs/4.save&insert.md)
    - [HQL和NativeSQL](./docs/5.hql&sql.md)