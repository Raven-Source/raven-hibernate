package org.raven.hibernate.jpa.test;//package org.raven.hibernate.jpa.test;
//
//import org.raven.hibernate.jpa.CountOptions;
//import org.raven.hibernate.jpa.test.repository.MassTask;
//import org.raven.hibernate.jpa.test.repository.MassTaskRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//
//@RunWith(SpringRunner.class)
////@ActiveProfiles("test")
//@SpringBootTest(classes = ApplicationTest.class)
//@Slf4j
//public class ClickhouseRepositoryTest {
//
//    @Autowired
//    private MassTaskRepository massTaskRepository;
//
//    @Test
//    public void groupByTest() {
//
//        // sort
//        CountOptions<MassTask> findOptions = CountOptions.empty(MassTask.class)
//                .groupBy(x -> x.groupBy(MassTask.Fields.dt, MassTask.Fields.status).build());
//
//        long count = massTaskRepository.count(findOptions);
//        System.out.println("count:" + count);
//
//    }
//
//
//}
