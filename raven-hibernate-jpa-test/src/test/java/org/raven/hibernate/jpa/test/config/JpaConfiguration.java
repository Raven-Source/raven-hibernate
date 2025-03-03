package org.raven.hibernate.jpa.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.raven.spring.commons.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @author yanfeng
 * date 2021.07.07 13:28
 */
@Configuration
@EnableTransactionManagement
@EntityScan(basePackageClasses = {Jsr310JpaConverters.class})
@EnableJpaAuditing()
public class JpaConfiguration {

    /**
     * 配置依赖，不可删除
     */
    final SpringContextUtils springContextUtils;

    /**
     * 配置依赖，不可删除
     */
    final ObjectMapper objectMapper;

    final DataSource dataSource;

    final JpaProperties jpaProperties;

    final HibernateProperties hibernateProperties;

    @Autowired
    public JpaConfiguration(SpringContextUtils springContextUtils
            , ObjectMapper objectMapper
            , DataSource dataSource
            , JpaProperties jpaProperties
            , HibernateProperties hibernateProperties) {

        this.springContextUtils = springContextUtils;
        this.objectMapper = objectMapper;
        this.dataSource = dataSource;
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
    }

    /**
     * 设置实体类所在位置
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = builder
                .dataSource(dataSource)
                .properties(jpaProperties.getProperties())// 设置jpa配置
                .properties(getVendorProperties())// 设置hibernate配置
                .mappingResources(jpaProperties.getMappingResources().toArray(new String[0]))
                .packages("org.raven.hibernate.jpa.test.repository")

                .persistenceUnit("primaryPersistenceUnit")
                .build();

        return entityManagerFactory;
    }

    private Map<String, ?> getVendorProperties() {
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder
            , @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
//        return new NoOpTransactionManager();
    }

    @Bean
    public HealthIndicator dbHealthIndicator() {

        DataSourceHealthIndicator indicator = new DataSourceHealthIndicator(dataSource);
        indicator.setQuery("SELECT 1");
        return indicator;
    }

}
