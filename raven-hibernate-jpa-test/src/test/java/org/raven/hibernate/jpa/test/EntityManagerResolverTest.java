package org.raven.hibernate.jpa.test;//package org.raven.hibernate.jpa.test;
//
//import org.raven.hibernate.jpa.test.repository.Orders;
//import org.raven.hibernate.jpa.test.repository.User;
//import org.raven.hibernate.utils.EntityManagerResolver;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.persistence.EntityManager;
//
//@RunWith(SpringRunner.class)
////@ActiveProfiles("test")
//@SpringBootTest(classes = ApplicationTest.class)
//@Slf4j
//public class EntityManagerResolverTest {
//
//    @Test
//    public void getEntityManager() {
//
//        EntityManager entityManager;
//
//        entityManager = EntityManagerResolver.getEntityManager(User.class);
//        System.out.println(entityManager);
//
//        entityManager = EntityManagerResolver.getEntityManager(Orders.class);
//        System.out.println(entityManager);
//    }
//}
//
