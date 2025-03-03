create database testdb ON CLUSTER default_cluster;

create table testdb.t_user ON CLUSTER default_cluster
(
    id           Int64,
    name         Nullable(String),
    tenant_id    Nullable(Int64),
    user_type    Nullable(Int32),
    time         Nullable(DateTime64(6)),
    channel_type Nullable(String),
    create_time  Nullable(DateTime64(6)),
    deleted      Nullable(Bool),
    _sign        Int8 materialized 1,
    _version     UInt64 materialized 1
)
    engine = VersionedCollapsingMergeTree(_sign, _version)
        PARTITION BY intDiv(id, 18446744073709551)
        ORDER BY tuple(id)
        SETTINGS index_granularity = 8192;


create table testdb.t_orders ON CLUSTER default_cluster
(
    id           Int64,
    uid          Nullable(Int64),
    price        Nullable(Decimal32(6)),
    box          Nullable(String),
    is_pay       Nullable(Bool),

    name         Nullable(String),
    refs         Array(Int32),
    status       Int8,
    version      UInt64,
    codes        Array(String),


    tenant_id    Nullable(Int64),
    user_type    Nullable(Int32),
    time         Nullable(DateTime64(6)),
    channel_type Nullable(String),

    create_time  Nullable(DateTime64(6)),
    update_time  Nullable(DateTime64(6)),
    deleted      Nullable(Bool),
    _sign        Int8 materialized 1,
    _version     UInt64 materialized 1
)
    engine = VersionedCollapsingMergeTree(_sign, _version)
        PARTITION BY intDiv(id, 18446744073709551)
        ORDER BY tuple(id)
        SETTINGS index_granularity = 8192;


truncate table testdb.t_user  ON CLUSTER default_cluster;
truncate table testdb.t_orders  ON CLUSTER default_cluster;

