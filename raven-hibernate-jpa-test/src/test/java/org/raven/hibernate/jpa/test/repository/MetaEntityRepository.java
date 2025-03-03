package org.raven.hibernate.jpa.test.repository;

import org.raven.hibernate.jpa.JpaRepositorySupport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaEntityRepository extends JpaRepositorySupport<MetaEntity, Long> {

    @Query("select t from MetaEntity t")
    List<MetaEntity> getList();

}
