package org.raven.hibernate.jpa.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.raven.hibernate.jpa.test.repository.Orders;
import org.raven.hibernate.jpa.test.repository.SimpleOrders;
import org.raven.hibernate.jpa.test.repository.User;
import org.raven.serializer.withJackson.JsonUtils;
import org.raven.spring.commons.config.JacksonConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * date 2022/8/9 16:53
 */
@SpringBootApplication(exclude = {
}, scanBasePackages = "org.raven")
@EnableConfigurationProperties(value = {
        DataSourceProperties.class
})
@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
@Import({JsonUtils.class, JacksonConfiguration.class})
public class JsonUtilTest {

    @Test
    public void test()
            throws Exception {


        User user = new User();
        String json = JsonUtils.toJsonString(user);
        System.out.println(json);
        user = JsonUtils.parseObject(json, User.class);
        System.out.println(user);

        System.out.println(
                JsonUtils.toJsonString("dsgadsgasdg")
        );

        Orders order = new Orders();

        String value = JsonUtils.toJsonString(order);
        System.out.println(value);

        Map map = JsonUtils.convert(order
                , Map.class);

        SimpleOrders simpleOrders = JsonUtils.convert(order, SimpleOrders.class);

        System.out.println(JsonUtils.toJsonString(order));
        System.out.println(JsonUtils.toJsonString(simpleOrders));

        Orders order2 = JsonUtils.convert(order, Orders.class);
        System.out.println(JsonUtils.toJsonString(order2));

    }


}