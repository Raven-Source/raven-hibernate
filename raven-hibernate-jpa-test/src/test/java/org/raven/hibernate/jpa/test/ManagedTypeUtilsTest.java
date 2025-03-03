package org.raven.hibernate.jpa.test;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.raven.hibernate.jpa.test.repository.Orders;
import org.raven.hibernate.jpa.test.repository.UserRepository;
import org.raven.hibernate.util.ManagedTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.metamodel.EntityType;
import java.util.Map;
import java.util.Set;

@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
@Slf4j
public class ManagedTypeUtilsTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void attributeNames() {

        EntityType<Orders> entityType = userRepository.entityManager().getMetamodel().entity(Orders.class);
        Set<String> list = ManagedTypeUtils.attributeNames(entityType);
        for (String s : list) {
            System.out.println(s);
        }

        AttributeDefinition attributeDefinition = ManagedTypeUtils.getAttributeDefinition(entityType, Orders.Fields.refs);
        Assert.assertTrue(attributeDefinition != null);

        Map<String, String> map = ManagedTypeUtils.columnsMapAttribute(entityType);
        Assert.assertTrue(map != null);
        System.out.println(map);
    }

}
