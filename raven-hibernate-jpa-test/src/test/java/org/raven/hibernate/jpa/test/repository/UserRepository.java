package org.raven.hibernate.jpa.test.repository;

import org.raven.hibernate.jpa.JpaRepositorySupport;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepositorySupport<User, Long> {

    @Override
    @Transactional
    @Modifying
    @Query(value = " truncate table t_user ", nativeQuery = true)
    void deleteAll();
}
