package org.raven.hibernate.jpa.test.config;

import lombok.RequiredArgsConstructor;
import org.raven.hibernate.jpa.impl.JpaRepositoryImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author yanfeng
 * date 2021.07.07 13:28
 */
@Configuration
@AutoConfigureAfter(JpaConfiguration.class)
@EnableJpaRepositories(
    repositoryBaseClass = JpaRepositoryImpl.class,
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager",
    basePackages = {"org.raven.hibernate.jpa.test.repository"})
@EntityScan(basePackages = "org.raven.hibernate.jpa.test.repository")
@RequiredArgsConstructor()
public class JpaRepositoryConfiguration {

}
