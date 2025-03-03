create table entity
(
    id            bigint auto_increment
        primary key,
    create_time   datetime(6)           null,
    creator       bigint                null,
    creator_name  varchar(255)          null,
    modifier      bigint                null,
    modifier_name varchar(255)          null,
    update_time   datetime(6)           null,
    deleted       boolean default false not null,
    tenant_id     bigint                null,
    code          varchar(255)          null,
    description   varchar(255)          null,
    name          varchar(255)          null
)
    engine = InnoDB;

create table `t_items`
(
    id   int auto_increment
        primary key,
    name varchar(255) null
)
    engine = InnoDB;

create table t_orders
(
    id          bigint auto_increment
        primary key,
    uid         bigint                                   null,
    items_id    bigint                                   null,
    price       decimal(19, 2)                           null,
    box         json                                     null,
    is_pay      bit                                      null,
    name        varchar(255)                             null,
    refs        json        default (_utf8mb4'[]')       not null,
    status      tinyint                                  null,
    version     bigint                                   null,
    deleted     boolean     default false                not null,
    create_time datetime(3) default (now(3))             not null,
    update_time datetime(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3),
    codes       json        default (_utf8mb4'[]')       null
)
    engine = InnoDB;

create index idx_codes
    on t_orders ((cast(json_extract(`codes`, _utf8mb4'$[*]') as char(20) array)));

create index idx_refs
    on t_orders ((cast(json_extract(`refs`, _utf8mb4'$[*]') as unsigned array)));

create table t_user
(
    id           bigint auto_increment
        primary key,
    create_time  datetime(6)           null,
    deleted      boolean default false not null,
    name         varchar(255)          null,
    tenant_id    bigint                null,
    user_type    int                   null,
    time         time                  null,
    channel_type varchar(20)           null
)
    engine = InnoDB;

