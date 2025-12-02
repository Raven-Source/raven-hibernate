-- 创建数据库
CREATE DATABASE IF NOT EXISTS testdb;

-- 用户表
CREATE TABLE testdb.t_user
(
    id           Int64,
    name         Nullable(String),
    tenant_id    Nullable(Int64),
    user_type    Nullable(Int32),
    time         Nullable(DateTime64(6)),
#     channel_type Nullable(String),
    create_time  Nullable(DateTime64(6)),
    deleted      Nullable(Bool),

    _version UInt64
)
    ENGINE = ReplacingMergeTree(_version)
    PARTITION BY intDiv(id, 1000000)  -- 分区规则可根据实际数据量调整
ORDER BY (id)
SETTINGS index_granularity = 8192;

-- 订单表
CREATE TABLE testdb.t_orders
(
    id           Int64,
    uid          Nullable(Int64),
    items_id     Nullable(Int64),
    name         Nullable(String),
    is_pay       Nullable(Bool),
    price        Nullable(Decimal32(6)),
    status       Int8,
    version      UInt64,
    box          Nullable(String),
    refs         Array(Int32),
    codes        Array(String),

--     tenant_id    Nullable(Int64),
--     user_type    Nullable(Int32),
--     time         Nullable(DateTime64(6)),
--     channel_type Nullable(String),

    create_time  Nullable(DateTime64(6)),
    update_time  Nullable(DateTime64(6)),
    deleted      Nullable(Bool),

    _version UInt64
)
    ENGINE = ReplacingMergeTree(_version)
    PARTITION BY intDiv(id, 1000000)  -- 分区规则可根据实际数据量调整
ORDER BY (id)
SETTINGS index_granularity = 8192;


-- 用户表
CREATE TABLE testdb.t_items
(
    id           Int64,
    name         Nullable(String),
    is_open      Nullable(Bool),

    _version UInt64
)
    ENGINE = ReplacingMergeTree(_version)
    PARTITION BY intDiv(id, 1000000)  -- 分区规则可根据实际数据量调整
ORDER BY (id)
SETTINGS index_granularity = 8192;