package org.raven.hibernate.jpa.test;//package org.raven.hibernate.jpa.test;
//
//import java.sql.Connection;
//import java.sql.Statement;
//import java.util.Arrays;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//public class JdbcInterceptor {
//
//    public JdbcInterceptor() {
//        System.out.println("JdbcInterceptor");
//    }
////    @Before("execution(* javax.sql.DataSource.getConnection(..))")
////    public void beforeGetConnection(JoinPoint joinPoint) throws Throwable {
////        System.out.println("Before getConnection()");
////    }
////
////    @After("execution(* javax.sql.DataSource.getConnection(..))")
////    public void afterGetConnection(JoinPoint joinPoint) throws Throwable {
////        System.out.println("After getConnection()");
////        Object[] args = joinPoint.getArgs();
////        if (args != null && args.length > 0) {
////            Connection connection = (Connection) args[0];
////            Statement statement = connection.createStatement();
////            statement.close();
////            connection.close();
////        }
////    }
//
//    @Before("execution(* java.sql.Statement.executeQuery(String)) && args(sql)")
//    public void beforeExecuteQuery(JoinPoint joinPoint, String sql) throws Throwable {
//        System.out.println("Before executeQuery(): " + sql);
//    }
//
//    @After("execution(* java.sql.Statement.executeQuery(String)) && args(sql)")
//    public void afterExecuteQuery(JoinPoint joinPoint, String sql) throws Throwable {
//        System.out.println("After executeQuery(): " + sql);
//    }
//
//    @Pointcut("@within(org.hibernate.annotations.*)")
//    public void hibernateAnnotatedClasses() {}
//
//    @Before("hibernateAnnotatedClasses()")
//    public void beforeHibernateMethod(JoinPoint joinPoint) throws Throwable {
//        System.out.println("Before Hibernate method: " + joinPoint.getSignature().getName());
//    }
//
//    @After("hibernateAnnotatedClasses()")
//    public void afterHibernateMethod(JoinPoint joinPoint) throws Throwable {
//        System.out.println("After Hibernate method: " + joinPoint.getSignature().getName());
//    }
//
//}
