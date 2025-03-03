package org.raven.hibernate.jpa.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.concurrent.CountDownLatch;

/**
 * @author yi.liang
 * date 2021.07.06 11:18
 */
@SpringBootApplication(exclude = {
}, scanBasePackages = "org.raven")
@EnableConfigurationProperties(value = {
        DataSourceProperties.class
})
@EnableAspectJAutoProxy
public class ApplicationTest {


    public static void main(String[] args) throws InterruptedException {

        SpringApplication springApplication = new SpringApplication(ApplicationTest.class);
        springApplication.run(args);

        // 阻塞主线程，保持程序运行
        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered, releasing latch...");
            latch.countDown();
        }));

        latch.await(); // 阻塞直到 latch.countDown() 被调用


    }
}
