package org.raven.hibernate.jpa.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.raven.hibernate.jpa.test.repository.Orders;
import org.raven.hibernate.jpa.test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import java.util.List;

@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
@Slf4j
public class FilterBuilderTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() {
        EntityManager entityManager = userRepository.entityManager();

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Orders> criteriaQuery = criteriaBuilder.createQuery(Orders.class);

        Root<Orders> root = criteriaQuery.from(Orders.class);
        root.fetch("user", JoinType.LEFT);

        criteriaQuery.select(root);

        // 执行查询
        List<Orders> result = entityManager.createQuery(criteriaQuery).getResultList();

        EntityType<Orders> entityType = root.getModel();

        System.out.println(entityType.getJavaType());

    }
}
