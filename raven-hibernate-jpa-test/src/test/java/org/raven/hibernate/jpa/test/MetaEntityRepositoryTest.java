package org.raven.hibernate.jpa.test;//package org.raven.hibernate.jpa.test;
//
//import org.raven.commons.constant.CommonConstant;
//import org.raven.hibernate.jpa.test.ApplicationTest;
//import org.raven.hibernate.jpa.test.repository.MetaEntity;
//import org.raven.hibernate.jpa.test.repository.MetaEntityRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.raven.commons.context.ContextHolder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//
//@RunWith(SpringRunner.class)
////@ActiveProfiles("test")
//@SpringBootTest(classes = ApplicationTest.class)
//@Slf4j
//public class MetaEntityRepositoryTest {
//
//    @Autowired
//    private ContextHolder contextHolder;
//
//    @Autowired
//    private MetaEntityRepository metaEntityRepository;
//
//    @Before
//    public void init() {
//
//        contextHolder.getContext().setAttribute(CommonConstant.TENANT_ID, 99L);
//        metaEntityRepository.deleteAll();
//    }
//
//    @Test
//    public void entityTest() {
//
//        contextHolder.getContext().setAttribute(CommonConstant.TENANT_ID, 99L);
//        int seed = 10;
//
//        for (int i = 0; i < seed; i++) {
//
//            MetaEntity metaEntity = new MetaEntity();
//            metaEntity.setName("打算离开攻击速度快");
//            metaEntity.setCode(String.valueOf(i));
//            metaEntity.setCreator(0L);
//            metaEntity.setModifier(0L);
//
//            metaEntityRepository.insert(metaEntity);
//        }
//
//        List<MetaEntity> list = metaEntityRepository.getList();
//        long id = list.get(0).getId();
//
//        MetaEntity metaEntity = metaEntityRepository.findOne(id);
//        metaEntity.setName("大厦开始");
//
//        metaEntityRepository.save(metaEntity);
//
//        list = metaEntityRepository.findAll();
//        System.out.println(list);
//
//        Assert.assertEquals(list.size(), seed);
//
//        contextHolder.getContext().setAttribute(CommonConstant.TENANT_ID, 100L);
//
//        list = metaEntityRepository.findAll();
//        Assert.assertEquals(list.size(), 0);
//
//    }
//
//}
