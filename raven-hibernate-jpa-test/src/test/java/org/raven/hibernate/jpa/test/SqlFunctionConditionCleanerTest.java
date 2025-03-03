package org.raven.hibernate.jpa.test;

import org.junit.jupiter.api.Test;
import org.raven.hibernate.sql.SqlFunctionConditionCleaner;

public class SqlFunctionConditionCleanerTest {

    @Test
    public void optimizationTest() {
        SqlFunctionConditionCleaner cleaner = new SqlFunctionConditionCleaner();
        String sql = cleaner.optimize("select orders0_.id as id1_2_, orders0_.create_time as create_t2_2_, orders0_.box as box3_2_, orders0_.codes as codes4_2_, orders0_.deleted as is_delet5_2_, orders0_.is_pay as is_pay6_2_, orders0_.name as name7_2_, orders0_.price as price8_2_, orders0_.refs as refs9_2_, orders0_.status as status10_2_, orders0_.uid as uid11_2_, orders0_.update_time as update_12_2_, orders0_.version as version13_2_ from t_orders orders0_ where json_contains(json_extract(orders0_.refs, '$[*]'), json_extract(?, '$[*]'))=1 and orders0_.deleted=? limit ?");

        System.out.println(sql);
    }
}
