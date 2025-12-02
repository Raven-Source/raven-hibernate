package org.raven.hibernate.jpa.test.config;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.datasource.driver-class-name", havingValue = "com.clickhouse.jdbc.ClickHouseDriver")
    public DataSource dataSource(DataSourceProperties  properties) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(properties.getUrl());
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setDriverClassName(properties.getDriverClassName());

        // 复制更多 Hikari 配置
        config.setMaximumPoolSize(1000);
        config.setMinimumIdle(10);
        config.setAutoCommit(true);

        return new ClickHouseHikariDataSource(config);
    }
}
