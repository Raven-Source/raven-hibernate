package org.raven.hibernate.jpa.test;//package org.raven.hibernate.jpa.test;
//
//import org.raven.hibernate.sql.BatchInsert;
//import org.junit.Test;
//
//public class BatchInsertTest {
//
//    @Test
//    public void toSqlTest() {
//
//        BatchInsert batchInsert = new BatchInsert();
//        batchInsert.setTableName("t_orders")
//                .setRows(3)
//                .addColumn("id")
//                .addColumn("name")
//                .addColumn("price")
//                .addColumn("status")
//                .addColumn("create_time");
//
//
//        System.out.println(batchInsert.toStatementString());
//
//    }
//
//}
