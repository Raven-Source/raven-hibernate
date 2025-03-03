package org.raven.hibernate.jpa.test.repository;

import org.raven.hibernate.jpa.JpaRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public interface MassTaskRepository extends JpaRepositorySupport<MassTask, Long> {
}
