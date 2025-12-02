package org.raven.hibernate.jpa.test.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ClickHouseHikariDataSource extends HikariDataSource {

    public ClickHouseHikariDataSource(HikariConfig config) {
        super(config);
    }
    @Override
    public Connection getConnection() throws SQLException {
        return new ClickHouseConnectionWrapper(super.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new ClickHouseConnectionWrapper(super.getConnection(username, password));
    }
}
